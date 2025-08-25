import psycopg2
from psycopg2 import sql
from psycopg2.extras import DictCursor
import datetime

dt = datetime.datetime.now()

def db_connect():
    return psycopg2.connect(
    host="localhost",
    database="adFactoryDB2",
    port=15432,
    user="postgres",
    password="@dtek1977"
)

count = 0
with db_connect() as conn:
    with conn.cursor(cursor_factory = DictCursor) as cur:
        cur.execute("SELECT m.material_no, m.location_id, m.in_stock_num FROM trn_material m LEFT JOIN mst_location l ON l.location_id = m.location_id WHERE m.location_id IS NOT NULL AND l.location_id IS NULL")
        #cur.execute("SELECT m.material_no, m.location_id, m.in_stock_num FROM trn_material m LEFT JOIN mst_location l ON l.location_id = m.location_id WHERE m.location_id = 11713")
        materials = cur.fetchall()
        
        for material in materials:
            print(material)
            cur.execute(sql.SQL("SELECT * FROM mst_location_old WHERE location_id = {}").format(sql.Literal(material["location_id"])))
            location_old = cur.fetchone()

            if location_old != None:
                # print("棚マスタあり [OLD] => ", material["location_id"], location_old["area_name"], location_old["location_no"])

                cur.execute("SELECT location_id FROM mst_location WHERE area_name = %s AND location_no = %s", [location_old["area_name"], location_old["location_no"]])
                location = cur.fetchone()
            
                if location != None:
                    print("棚IDの更新1 => ", material["material_no"], material["location_id"], location["location_id"], location_old["area_name"], location_old["location_no"])
                    cur.execute("UPDATE trn_material SET location_id = %s WHERE material_no = %s", [location["location_id"], material["material_no"]])
                    conn.commit()
                else:
                    cur.execute(sql.SQL("SELECT location_id FROM mst_location WHERE location_id = {}").format(sql.Literal(material["location_id"])))
                    location_id = cur.fetchone()

                    if location_id == None:
                        print("棚マスタの追加 => ", material["location_id"], location_old["area_name"], location_old["location_no"])
                        cur.execute("INSERT INTO mst_location(location_id, area_name, location_no, create_date) VALUES (%s, %s, %s, %s)", [material["location_id"], location_old["area_name"], location_old["location_no"], dt])
                        conn.commit()
            else:
                cur.execute(sql.SQL("SELECT * FROM log_stock WHERE material_no = {} AND location_no IS NOT NULL ORDER BY event_id DESC").format(sql.Literal(material["material_no"])))
                logStocks = cur.fetchall()
                
                for logStock in logStocks:
                    
                    cur.execute("SELECT location_id, area_name, location_no FROM mst_location WHERE area_name = %s AND location_no = %s", [logStock["area_name"], logStock["location_no"]])
                    location_new = cur.fetchone()
                    
                    if location_new != None:
                        print("棚IDの更新2 => ", material["material_no"], material["location_id"], location_new["location_id"], location_new["area_name"], location_new["location_no"])
                        cur.execute("UPDATE trn_material SET location_id = %s WHERE material_no = %s", [location_new["location_id"], material["material_no"]])
                        conn.commit()
                        break

            count += 1
        
print(count)