using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Net.Http;
using System.Reflection;
using System.Security.Authentication;
using System.Security.Cryptography.X509Certificates;
using System.Text;
using System.Xml;
using System.Xml.Serialization;

namespace ExcelImport
{
    class AdFactoryClient
	{
		private const string ftpUsername = "adtek";
		private const string ftpPassword = "adtek";


		private static string hostName
		{
			get
			{
				return adWorkbookAddIn.Properties.Settings.Default.SERVER_HOSTNAME;
			}
		}

		private static string httpRestUrl
		{
			get
			{
				return "https://" + hostName + "/adFactoryServer/rest";
			}
		}

		private static string ftpUrl
		{
			get
			{
				return "ftp://" + hostName;
			}
		}

		public static HttpClient NewHttpClient()
		{
			var handler = new HttpClientHandler
			{
				SslProtocols = SslProtocols.Tls12 | SslProtocols.Tls11 | SslProtocols.Tls,
				ClientCertificateOptions = ClientCertificateOption.Manual,
				ServerCertificateCustomValidationCallback = (sender, cert, chain, errors) =>
				{
					return true;
				}
			};

			// 証明書が無くても接続できるようです
			//var certificate = new X509Certificate2(GetCertificateKey(), "adtekfuji", X509KeyStorageFlags.MachineKeySet);
			//handler.ClientCertificates.Add(certificate);

			return new HttpClient(handler);
		}

		/// <summary>
		/// 証明書キーを取得する。
		/// </summary>
		/// <returns></returns>
		private static byte[] GetCertificateKey()
		{
			var assembly = Assembly.GetExecutingAssembly();
			using (var stream = assembly.GetManifestResourceStream("adWorkbookAddIn.Resources.newcert.p12"))
			{
				byte[] rowData = new byte[(int)stream.Length];
				stream.Read(rowData, 0, rowData.Length);
				return rowData;
			}
		}

		public static ResponseType DoHttpGetRequest<ResponseType>(HttpClient client, string path)
		{
			var responseTask = client.GetAsync(httpRestUrl + path);
			var response = responseTask.Result.EnsureSuccessStatusCode();

			XmlSerializer responseSerializer = new XmlSerializer(typeof(ResponseType));
			var responseEntity = (ResponseType)responseSerializer.Deserialize(response.Content.ReadAsStreamAsync().Result);
			return responseEntity;
		}

		public static ResponseType DoHttpPostRequest<RequestType, ResponseType>(HttpClient client, string path, RequestType req)
		{
			string content;
			using (var stream = new System.IO.MemoryStream())
			{
				var settings = new XmlWriterSettings
				{
					Indent = false, // インデントを無効化
					OmitXmlDeclaration = false, // XML宣言を省略
					Encoding = new UTF8Encoding(false) // BOMを排除
				};

				using (var writer = XmlWriter.Create(stream, settings))
				{
					var xns = new XmlSerializerNamespaces();
					xns.Add("", "");
					new XmlSerializer(typeof(RequestType)).Serialize(writer, req, xns);
				}

				content = Encoding.UTF8.GetString(stream.ToArray());
			}

			var stringContent = new StringContent(content, Encoding.UTF8, "application/xml");
			var task = client.PostAsync(httpRestUrl + path, stringContent);

			var res = (ResponseType) new XmlSerializer(typeof(ResponseType)).Deserialize(task.Result.Content.ReadAsStreamAsync().Result);

			if (!task.Result.IsSuccessStatusCode)
			{
				if (res is ResponseEntity)
                {
					var response  = res as ResponseEntity;
					var message = response.errorType;
					switch (response.errorType)
                    {
						case "IDENTNAME_OVERLAP":
							break;
						case "NOT_ACCESS_RESOURCE":
							message = adWorkbookAddIn.Source.LocaleUtil.GetString("NOT_ACCESS_RESOURCE");
							break;
						case "LOCKED_RESOURCE":
							message = adWorkbookAddIn.Source.LocaleUtil.GetString("LOCKED_RESOURCE");
							break;
					}

					throw new Exception(message);
				}

				throw new Exception(task.Result.StatusCode.ToString());
			}

			return res;
		}

		public static ResponseType DoHttpPutRequest<RequestType, ResponseType>(HttpClient client, string path, RequestType req)
		{
			string content;
			using (var stream = new System.IO.MemoryStream())
			{
				var settings = new XmlWriterSettings
				{
					Indent = false, // インデントを無効化
					OmitXmlDeclaration = false, // XML宣言を省略
					Encoding = new UTF8Encoding(false) // BOMを排除
				};

				using (var writer = XmlWriter.Create(stream, settings))
				{
					var xns = new XmlSerializerNamespaces();
					xns.Add("", "");
					new XmlSerializer(typeof(RequestType)).Serialize(writer, req, xns);
				}

				content = Encoding.UTF8.GetString(stream.ToArray());
			}

			var httpContent = new StringContent(content, Encoding.UTF8, "application/xml");

			var responseTask = client.PutAsync(httpRestUrl + path, httpContent);
			var response = responseTask.Result.EnsureSuccessStatusCode();

			XmlSerializer responseSerializer = new XmlSerializer(typeof(ResponseType));
			var responseEntity = (ResponseType)responseSerializer.Deserialize(response.Content.ReadAsStreamAsync().Result);
			return responseEntity;
		}

		public static WorkEntity GetWork(HttpClient client, int workId)
		{
			return DoHttpGetRequest<WorkEntity>(client, "/work/" + workId);
		}

		public static ResponseEntity AddWork(HttpClient client, WorkEntity workEntity)
		{
			return DoHttpPostRequest<WorkEntity, ResponseEntity>(client, "/work", workEntity);
		}

		public static ResponseEntity UpdateWork(HttpClient client, WorkEntity workEntity)
		{
			return DoHttpPutRequest<WorkEntity, ResponseEntity>(client, "/work", workEntity);
		}

		public static WorkflowEntity GetWorkflow(HttpClient client, int workflowId)
		{
			return DoHttpGetRequest<WorkflowEntity>(client, "/workflow/" + workflowId);
		}

		public static ResponseEntity AddWorkflow(HttpClient client, WorkflowEntity workflowkEntity)
		{
			return DoHttpPostRequest<WorkflowEntity, ResponseEntity>(client, "/workflow", workflowkEntity);
		}

		public static ResponseEntity UpdateWorkflow(HttpClient client, WorkflowEntity workflowEntity)
		{
			return DoHttpPutRequest<WorkflowEntity, ResponseEntity>(client, "/workflow", workflowEntity);
		}

		public static ResponseEntity ImportWorkflow(HttpClient client, ImportWorkflowEntity importWorkflowEntity)
		{
			string path = $"/workflow/import?loginId={adWorkbookAddIn.AdProperties.LoginID}";
			return DoHttpPostRequest<ImportWorkflowEntity, ResponseEntity>(client, path, importWorkflowEntity); ;
		}

		public static long? FindWorkHierarchyIdByName(HttpClient client, string workHierarchyName)
		{
			string path = $"/work/tree/hierarchy/name?name={Uri.EscapeDataString(workHierarchyName)}";
			WorkHierarchyEntity entity = DoHttpGetRequest<WorkHierarchyEntity>(client, path);
			return entity.workHierarchyId;
		}

		public static long? FindWorkflowHierarchyIdByName(HttpClient client, string workflowHierarchyName)
		{
			string path = $"/workflow/tree/hierarchy/name?name={Uri.EscapeDataString(workflowHierarchyName)}";
			WorkflowHierarchyEntity entity = DoHttpGetRequest<WorkflowHierarchyEntity>(client, path);
			return entity.workflowHierarchyId;
		}

		public static OrganizationEntity FindOrganizationByName(HttpClient client, string userName)
		{
			if (userName == null)
			{
				return null;
			}
			string path = $"/organization/name?name={Uri.EscapeDataString(userName)}";
			return DoHttpGetRequest<OrganizationEntity>(client, path);
		}

		public static long? FindUserIdByName(HttpClient client, string userName)
		{
			OrganizationEntity entity = FindOrganizationByName(client, userName);
			return entity?.organizationId;
		}

		public static List<OrganizationEntity> FindOrganizationByUserId(HttpClient client, long userId)
		{
			return DoHttpGetRequest<Organizations>(client, "/organization/range?user=" + userId).organizations;
		}

		public static long? FindEquipmentIdByName(HttpClient client, string name)
		{
			if (name == null)
			{
				return null;
			}
			string path = $"/equipment/name?name={Uri.EscapeDataString(name)}";
			EquipmentEntity entity = DoHttpGetRequest<EquipmentEntity>(client, path);
			return entity.equipmentId;
		}

		public static List<EquipmentEntity> FindEquipmentByUserId(HttpClient client, long userId)
		{
			return DoHttpGetRequest<Equipments>(client, "/equipment/range?user=" + userId).equipments;
		}

		public static bool CheckLicense(HttpClient client, string optionName)
		{
			return DoHttpGetRequest<SystemOptionEntity>(client, "/system/option/" + optionName).enable;
		}

		public static OrganizationLoginResult Login(HttpClient client, string id, string password)
		{
			var request = new OrganizationLoginRequest
			{
				loginType = LoginType.PASSWORD,
				loginId = id,
				authData = Utils.GetHashSha256(password)
			};
			return DoHttpPutRequest<OrganizationLoginRequest, OrganizationLoginResult>(client, "/organization/login?withAuth=true", request);
		}

		public static void FtpCreateDirectory(string path)
		{
			try
			{
				var reqFTP = (FtpWebRequest)FtpWebRequest.Create(path);
				reqFTP.Method = WebRequestMethods.Ftp.MakeDirectory;
				reqFTP.UsePassive = false;
				reqFTP.UseBinary = true;
				reqFTP.Credentials = new NetworkCredential(ftpUsername, ftpPassword);
				using (FtpWebResponse response = (FtpWebResponse)reqFTP.GetResponse())
				{
					using (var ftpStream = response.GetResponseStream()) { }
				}
			}
			catch (WebException ex)
			{
				var ftpResponse = ex.Response as FtpWebResponse;
				var statusCode = ftpResponse.StatusCode;
				if (statusCode != FtpStatusCode.ActionNotTakenFileUnavailable)
				{
					throw ex;
				}
			}
		}

		public static void UploadImage(long workId, string serverFileName, Stream inStream)
		{
			string path = "/data/pdoc/" + workId;
			FtpCreateDirectory(ftpUrl + path);

			var reqFTP = (FtpWebRequest)FtpWebRequest.Create(ftpUrl + path + "/" + serverFileName);
			reqFTP.Method = WebRequestMethods.Ftp.UploadFile;
			reqFTP.UsePassive = false;
			reqFTP.UseBinary = true;
			reqFTP.Credentials = new NetworkCredential(ftpUsername, ftpPassword);

			using (var outStream = reqFTP.GetRequestStream())
			{
				inStream.CopyTo(outStream);
			}
		}
	}
}
