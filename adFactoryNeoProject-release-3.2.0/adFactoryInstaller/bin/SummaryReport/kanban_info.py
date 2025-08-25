# 'pip install pandas'
# 'pip install psycopg2'
# 'dataset' はこのスクリプトの入力データを保持しています
import datetime
import psycopg2
import pandas as pd
import sys

# 接続情報
connection_config = {
    'host': 'localhost',
    'port': '15432',
    'database': 'adFactoryDB2',
    'user': 'postgres',
    'password': '@dtek1977'
}


class WorkDateTime:
    def __init__(self, entity, date):
        self.entity = entity
        self.date = date

    def get_tact_time(self):
        return self.entity["takt_time"] / 1000 if "takt_time" in self.entity else 0

    def get_date(self):
        return self.date

    def get_entity(self):
        return self.entity


class WorkStartDateTime(WorkDateTime):
    def update_list(self, list1):
        list1.append(self.entity)
        return list1


class WorkEndDateTime(WorkDateTime):
    def update_list(self, list1):
        return [s for s in list1 if s["actual_id"] != self.entity["actual_id"]]


def remove_beak_time(actual_data, df_break_time):
    organization_id = actual_data['organization_id']
    work_times = [(actual_data['start_datetime'], actual_data['comp_datetime'])]

    if organization_id not in df_break_time:
        return work_times

    for break_start_time, break_end_time in df_break_time.loc[organization_id]:
        tmp = []
        for work_start, work_comp in work_times:
            base_date = datetime.datetime.combine(work_start.date(), datetime.time(0, 0, 0))
            while base_date < work_comp:
                break_start = base_date + break_start_time
                break_end = base_date + break_end_time
                if work_start <= break_start < work_comp:
                    tmp.append((work_start, break_start))
                    work_start = break_end

                # 1日進める
                base_date += datetime.timedelta(days=1)

            if work_start < work_comp:
                tmp.append((work_start, work_comp))

        work_times = tmp

    return work_times


def create_actual_work_info(cur, model_name, start_datetime, comp_datetime):
    print("create_actual_work_info")

    actual_data_sql = "SELECT tar.actual_id, tar.kanban_id, tarp.kanban_name, t.model_name, tarp.work_kanban_id, tarp.workflow_id, tarp.workflow_name, tarp.work_id, tarp.work_name, tarp.organization_id, tarp.organization_name, tarp.equipment_id, tarp.equipment_name, tarp.implement_datetime start_datetime, tar.implement_datetime  comp_datetime, t.actual_comp_datetime date, tar.delay_reason_id, tar.delay_reason, t.takt_time, t.kanban_comp FROM trn_actual_result tar JOIN (SELECT twk.work_kanban_id, twk.takt_time, twk.actual_comp_datetime, k.model_name, twk.actual_comp_datetime=k.actual_comp_datetime kanban_comp FROM trn_work_kanban twk JOIN (SELECT tk.kanban_id, tk.model_name, tk.actual_comp_datetime FROM trn_kanban tk WHERE tk.model_name IN (%s)) k ON k.kanban_id = twk.kanban_id WHERE twk.work_status = 'COMPLETION' AND %s <= twk.actual_comp_datetime AND twk.actual_comp_datetime < %s) t ON tar.pair_id NOTNULL AND (tar.actual_status = 'COMPLETION' OR tar.actual_status = 'SUSPEND') AND t.work_kanban_id = tar.work_kanban_id JOIN trn_actual_result tarp ON tar.pair_id = tarp.actual_id"
    cur.execute(actual_data_sql, (model_name, start_datetime, comp_datetime))
    data = cur.fetchall()
    df_actual_data = pd.DataFrame(data, columns=[col.name for col in cur.description])

    work_kanban_works = {}
    for index, actual_data in df_actual_data.iterrows():
        work_kanban_id = actual_data['work_kanban_id']
        start_time = actual_data['start_datetime']
        comp_time = actual_data['comp_datetime']
        work_kanban_works.setdefault(work_kanban_id, [])
        work_kanban_works[work_kanban_id].append(WorkStartDateTime(actual_data, start_time))
        work_kanban_works[work_kanban_id].append(WorkEndDateTime(actual_data, comp_time))

    # 作業時間と遅延時間を計算
    work_and_delay_time = {}
    for work_kanban_id, work_date_times in work_kanban_works.items():
        if len(work_date_times) <= 0:
            continue

        work_and_delay_time.setdefault(work_kanban_id, {})
        work_date_times.sort(key=lambda x: x.get_date())
        delay_time = -work_date_times[0].get_tact_time()

        table = []
        for n in range(len(work_date_times)):
            if len(table) > 0:
                work_time = (work_date_times[n].get_date() - work_date_times[n - 1].get_date()).total_seconds()
                delay_time += work_time * len(table)
                add_delay_time = 0

                if delay_time > 0:
                    add_delay_time = delay_time / len(table)
                    delay_time = 0

                for entity in table:
                    actual_id = entity['actual_id']
                    work_and_delay_time[work_kanban_id].setdefault(actual_id,
                                                                   {'work_time': 0, 'delay_time': 0, 'entity': entity})
                    work_and_delay_time[work_kanban_id][actual_id]['work_time'] += work_time
                    work_and_delay_time[work_kanban_id][actual_id]['delay_time'] += add_delay_time
                    work_and_delay_time[work_kanban_id][actual_id]['comp_datetime'] = work_date_times[n].get_date()

            table = work_date_times[n].update_list(table)

    # 理由が割り当たっていない物の辻褄を合わせる
    for work_kanban_id, value in work_and_delay_time.items():
        work_and_delay_list = [val for val in value.values()]
        work_and_delay_list.sort(key=lambda x: x['comp_datetime'])
        delay_time = 0

        for data in work_and_delay_list:
            entity = data["entity"]
            actual_id = entity["actual_id"]

            if pd.isna(entity["delay_reason"]):
                delay_time += work_and_delay_time[work_kanban_id][actual_id]['delay_time']
                work_and_delay_time[work_kanban_id][actual_id]['delay_time'] = 0
                continue

            work_and_delay_time[work_kanban_id][actual_id]['delay_time'] += delay_time
            delay_time = 0

        if delay_time > 0:
            entity = work_and_delay_list[-1]["entity"]
            work_kanban_id = entity["work_kanban_id"]
            entity["delay_reason"] = "NoRegisterReason"
            work_and_delay_time[work_kanban_id][entity["actual_id"]]['delay_time'] = delay_time

    # csv出力
    ret = []
    for work_kanban_id, value in work_and_delay_time.items():
        for actual_id, work_and_delay in value.items():
            entity = work_and_delay["entity"]
            tmp = {
                'work_kanban_id': work_kanban_id,
                'organization_id': entity['organization_id'],
                'equipment_id': entity['equipment_id'],
                'work_time': int(work_and_delay['work_time']),
                'delay_time': int(work_and_delay['delay_time']),
                "delay_reason": entity["delay_reason"],
            }
            ret.append(tmp)

    return pd.DataFrame(ret)


def create_interrupt_work_info(cur, model_name, start_datetime, comp_datetime):
    print("create_interrupt_work_info")
    # 中断時間を取得
    actual_data_sql = "SELECT tar.actual_id, tarp.kanban_id, tarp.kanban_name, t.model_name, tarp.work_kanban_id, tarp.workflow_id, tarp.workflow_name, tarp.work_id, tarp.work_name, tarp.organization_id, tarp.organization_name, tarp.equipment_id, tarp.implement_datetime start_datetime, tar.implement_datetime comp_datetime, t.actual_comp_datetime date, tar.interrupt_reason_id, tar.interrupt_reason, t.takt_time FROM trn_actual_result tar JOIN (SELECT twk.work_kanban_id, twk.takt_time, twk.actual_comp_datetime, k.model_name FROM trn_work_kanban twk JOIN (SELECT tk.kanban_id, tk.model_name FROM trn_kanban tk WHERE tk.model_name IN (%s)) k ON k.kanban_id = twk.kanban_id WHERE %s<=twk.actual_comp_datetime AND twk.actual_comp_datetime<%s AND twk.work_status = 'COMPLETION') t ON tar.pair_id NOTNULL AND tar.actual_status = 'WORKING' AND t.work_kanban_id = tar.work_kanban_id AND (tar.assist IS NULL OR tar.assist = 0) JOIN trn_actual_result tarp ON tar.pair_id = tarp.actual_id AND tarp.actual_status='SUSPEND'"
    cur.execute(actual_data_sql, (model_name, start_datetime, comp_datetime))
    data = cur.fetchall()
    df_actual_data = pd.DataFrame(data, columns=[col.name for col in cur.description])

    df_actual_data["interrupt_time"] = 0
    for index, actual_data in df_actual_data.iterrows():
        df_actual_data.loc[index, "interrupt_time"] = (
                actual_data['comp_datetime'] - actual_data['start_datetime']).seconds

    return pd.DataFrame(df_actual_data[[
        "work_kanban_id",
        "organization_id",
        "equipment_id",
        "interrupt_reason",
        "interrupt_time"
    ]])


def create_kanban_info(cur, model_name, start_datetime, comp_datetime):
    print("create_kanban_info")
    # カンバン情報取得
    sql = "SELECT k.kanban_id, k.kanban_name, k.model_name, mw.workflow_name, k.actual_start_datetime, k.actual_comp_datetime FROM (SELECT tk.kanban_id, tk.kanban_name, tk.model_name, tk.actual_start_datetime, tk.actual_comp_datetime, tk.workflow_id FROM trn_kanban tk WHERE tk.model_name IN (%s) AND %s<=tk.actual_comp_datetime AND tk.actual_comp_datetime<%s AND tk.kanban_status='COMPLETION') k JOIN mst_workflow mw ON k.workflow_id = mw.workflow_id"
    cur.execute(sql, (model_name, start_datetime, comp_datetime))
    data = cur.fetchall()
    return pd.DataFrame(data, columns=[col.name for col in cur.description])


def create_work_kanban_info(cur, model_name, start_datetime, comp_datetime):
    print("create_kanban_info")
    # 工程カンバン情報取得
    sql = "SELECT twk.kanban_id, twk.work_kanban_id, mw.work_name, twk.actual_start_datetime, twk.actual_comp_datetime FROM (SELECT tk.kanban_id, tk.kanban_name, tk.model_name, tk.actual_start_datetime, tk.actual_comp_datetime, tk.workflow_id FROM trn_kanban tk WHERE tk.model_name IN (%s) AND  %s<=tk.actual_comp_datetime AND tk.actual_comp_datetime<%s AND tk.kanban_status='COMPLETION') k JOIN trn_work_kanban twk ON k.kanban_id = twk.kanban_id JOIN mst_work mw ON twk.work_id = mw.work_id"
    cur.execute(sql, (model_name, start_datetime, comp_datetime))
    data = cur.fetchall()
    return pd.DataFrame(data, columns=[col.name for col in cur.description])


def create_organization_info(cur):
    print("create_organization_info")
    # 組織情報取得
    sql = "SELECT mo.organization_id, mo.organization_name FROM mst_organization mo"
    cur.execute(sql)
    data = cur.fetchall()
    return pd.DataFrame(data, columns=[col.name for col in cur.description])


def create_equipment_info(cur):
    print("create_equipment_info")
    sql = "SELECT me.equipment_id, me.equipment_name FROM mst_equipment me"
    cur.execute(sql)
    data = cur.fetchall()
    return pd.DataFrame(data, columns=[col.name for col in cur.description])


def main(model_name, start_datetime, comp_datetime):
    con = psycopg2.connect(**connection_config)
    cur = con.cursor()

    tables = [
        # カンバン情報出力
        ("kanban_info", create_kanban_info(cur, model_name, start_datetime, comp_datetime)),
        # 工程カンバン情報出力
        ("work_kanban_info", create_work_kanban_info(cur, model_name, start_datetime, comp_datetime)),
        # 組織情報出力
        ("organization_info", create_organization_info(cur)),
        # 設備情報出力
        ("equipment_info", create_equipment_info(cur)),
        # 中断情報出力
        ("interrupt_work_info",
         create_interrupt_work_info(cur, model_name, start_datetime, comp_datetime)),
        # 実績情報出力
        ("actual_work_info", create_actual_work_info(cur, model_name, start_datetime, comp_datetime))
    ]

    for file_name, table in tables:
        print(file_name)
        table.to_csv(file_name + ".csv", index=False, date_format='%Y-%m-%d')


if __name__ == '__main__':
    model_name = sys.argv[1]
    start_datetime = sys.argv[2]
    comp_datetime = sys.argv[3]

    # model_name = "Feeder"
    # start_datetime = "2020-1-1 00:00:00"
    # comp_datetime = "2021-7-30 23:59:59"

    # print(model_name)
    # print(start_datetime)
    # print(comp_datetime)
    # model_name = "testBreakTest2"
    # start_datetime = "2020-7-26 18:30:16"
    # comp_datetime = "2021-7-26 18:59:16"

    main(model_name, start_datetime, comp_datetime)
