/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.adtekfuji.adFactory.entity;

import jp.adtekfuji.adFactory.entity.dsKanban.DsKanban;
import jp.adtekfuji.adFactory.entity.dsKanban.DsPickup;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;

/**
 *
 * @author s-heya
 */
public class ServiceInfoEntityTest {
    
    @Test
    public void testDsKanban() throws Exception {
        System.out.println("testDsKanban");
        String serviceInfosStr = "[\n" +
                                "  {\n" +
                                "    \"service\": \"dsKanban\",\n" +
                                "    \"job\": {\n" +
                                "      \"category\": 1,\n" +
                                "      \"productNo\": \"157530-8410\",\n" +
                                "      \"productName\": \"H869 メータ\",\n" +
                                "      \"spec\": \"140L MID\",\n" +
                                "      \"package\": \"DS\",\n" +
                                "      \"note\": \"備考\",\n" +
                                "      \"actual\": [\n" +
                                "        {\n" +
                                "          \"key\": \"1\",\n" +
                                "          \"pn\": \"作業者\",\n" +
                                "          \"st\": \"開始日時\",\n" +
                                "          \"ct\": \"完了日時\",\n" +
                                "          \"wt\": 3600\n" +
                                "        },\n" +
                                "        {\n" +
                                "          \"key\": \"2\",\n" +
                                "          \"pn\": \"作業者\",\n" +
                                "          \"st\": \"2024/7/21 13:31\",\n" +
                                "          \"ct\": \"2024/7/21 14:23\",\n" +
                                "          \"wt\": 3600\n" +
                                "        },\n" +
                                "        {\n" +
                                "          \"key\": \"3\",\n" +
                                "          \"pn\": \"作業者\",\n" +
                                "          \"st\": \"2024/7/21 13:31\",\n" +
                                "          \"ct\": \"2024/7/21 14:23\",\n" +
                                "          \"wt\": 3600\n" +
                                "        },\n" +
                                "        {\n" +
                                "          \"key\": \"4\",\n" +
                                "          \"pn\": \"作業者\",\n" +
                                "          \"st\": \"2024/7/21 13:31\",\n" +
                                "          \"ct\": \"2024/7/21 14:23\",\n" +
                                "          \"wt\": 3600\n" +
                                "        },\n" +
                                "        {\n" +
                                "          \"key\": \"5\",\n" +
                                "          \"pn\": \"作業者\",\n" +
                                "          \"st\": \"2024/7/21 13:31\",\n" +
                                "          \"ct\": \"2024/7/21 14:23\",\n" +
                                "          \"wt\": 3600\n" +
                                "        },\n" +
                                "        {\n" +
                                "          \"key\": \"6\",\n" +
                                "          \"pn\": \"作業者\"\n" +
                                "        }\n" +
                                "      ]\n" +
                                "    }\n" +
                                "  }\n" +
                                "]";
        //try (FileInputStream inputStream = new FileInputStream("src\\test\\resources\\jp\\adtekfuji\\adfactory\\entity\\dsKanban.json");
        //        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
        //    serviceInfosStr = reader.lines().collect(Collectors.joining(System.lineSeparator()));
        //}
        DsKanban dsKanban = DsKanban.lookup(serviceInfosStr);
        assertThat(dsKanban.getCategory(), is(1));
        assertThat(dsKanban.getProductNo(), is("157530-8410"));
        assertThat(dsKanban.getProductName(), is("H869 メータ"));
        assertThat(dsKanban.getSpec(), is("140L MID"));
        assertThat(dsKanban.getPackageCode(), is("DS"));
        assertThat(dsKanban.getNote(), is("備考"));
        assertThat(dsKanban.getActuals().size(), is(6));
    }

    @Test
    public void testDsPickup() throws Exception {
        System.out.println("testDsPickup");
        String serviceInfosStr = "[\n" +
                                "  {\n" +
                                "    \"service\": \"dsPickup\",\n" +
                                "    \"job\": {\n" +
                                "      \"category\": 1,\n" +
                                "      \"productNo\": \"157530-8410\",\n" +
                                "      \"pickup\": [\n" +
                                "        {\n" +
                                "          \"key\": \"1\",\n" +
                                "          \"ic\": \"457873-0431\",\n" +
                                "          \"in\": \"MOTOR ASSY, STEPPING\",\n" +
                                "          \"qt\": 3,\n" +
                                "          \"pr\": \"\",\n" +
                                "          \"lo\": \"H876\",\n" +
                                "          \"pn\": \"作業者\",\n" +
                                "          \"ct\": \"2024/7/21 14:23\"\n" +
                                "        },\n" +
                                "        {\n" +
                                "          \"key\": \"2\",\n" +
                                "          \"ic\": \"057713-8060\",\n" +
                                "          \"in\": \"CASE, LWR\",\n" +
                                "          \"qt\": 1,\n" +
                                "          \"pr\": \"ラベル\",\n" +
                                "          \"lo\": \"\",\n" +
                                "          \"pn\": null,\n" +
                                "          \"ct\": null\n" +
                                "        }\n" +
                                "      ]\n" +
                                "    }\n" +
                                "  }\n" +
                                "]";
        //try (FileInputStream inputStream = new FileInputStream("src\\test\\resources\\jp\\adtekfuji\\adfactory\\entity\\dsPickup.json");
        //        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
        //    serviceInfosStr = reader.lines().collect(Collectors.joining(System.lineSeparator()));
        //}
        DsPickup dsPickup = DsPickup.lookup(serviceInfosStr);
        assertThat(dsPickup.getCategory(), is(1));
        assertThat(dsPickup.getProductNo(), is("157530-8410"));
        assertThat(dsPickup.getPartsList().size(), is(2));
    }
}
