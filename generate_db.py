import networkx
import shutil
import csv
import pandas
import sqlite3
from pathlib import Path
from collections import defaultdict

HEADER_NODOS = ["nodo", "nombre"]

CHECKPOINTS = [
	"fuente de agua biblioteca",       # 1
	"ascensor edificio principal",     # 2
	"puerta patio edificio principal", # 3 (dentro)
	"sala de estudio planta baja",     # 4
	"puerta edificio aulas",           # 5 (fuera)
	"ascensor planta baja",            # 6
	"ascensor 1 planta",               # 7
	"ascensor 2 planta",               # 8
	"ascensor 3 planta",               # 9
	"hall secretaria",                 # 10
	"escaleras emergencia 3 planta",   # 11 (fuera)
]

DESTINOS = [
	"biblioteca",         # -1
	"cafetería",          # -2
	"aula 3.5",           # -3
	"secretaría",         # -4
	"despacho marcelino", # -5 (3 planta despacho 21)
]

HEADER_INSTRUCCIONES = ["nodo1", "nodo2", "instrucciones", "imagen", "direccion"]
TABLA_INSTRUCCIONES = [
	('1', '-1', "Situése de manera que la fuente quede a mano izquierda, cruce la puerta, gire a la derecha y camine recto hasta encontrarse enfrente de la puerta.","imgs/imgnodo_1_-1", "0"),
	('1', '2', "Situese de espaldas a la fuente y baje las escaleras. Cuando salga de las escaleras, gire a la derecha y camine recto hasta tener el ascensor a su derecha.","imgs/imgnodo_1_2", "down"),
	('2', '3', "Situése de manera que la puerta del ascensor quede a su derecha. Camine recto y gire a la derecha para encontrarse con la puerta de salida del patio.","imgs/imgnodo_2_3", "0"),
	('2', '10', "Situése de manera que la puerta del ascensor quede a su izquierda. Camine recto hasta que pase entre las columnas que le llevan al hall de secretaría.","imgs/imgnodo_2_10", "0"),
	('3', '10', "Situese de espaldas a la puerta. Avance y pase entre las columnas que le llevan al hall de secretaría.","imgs/imgnodo_3_10", "0"),
	('3', '5', "Salga por la puerta del edificio principal, gire a la izquierda y camine recto hasta llegar a la puerta del edificio de aulas.","imgs/imgnodo_3_5", "0"),
	('3', '4', "Situése de cara a la puerta y baje por las escaleras de su derecha. A su izquierda se encuentra la puerta del aula de estudio.","imgs/imgnodo_3_4", "down"),
	('4', '-2', "Situése de espaldas a la puerta del aula de estudio. Camine recto y gire a la izquierda en dirección a la puerta de la cafeteria.","imgs/imgnodo_4_-2", "0"),
	('5', '6', "Entre al edificio, gire a la derecha y continúe por el pasillo hasta que a su derecha se encuentre la puerta del ascensor de la planta 0.","imgs/imgnodo_5_6", "0"),
	('6', '7', "Suba las escaleras hasta la primera planta, luego gire a la derecha y camine recto hasta que la puerta del ascensor se encuentre a su izquierda.","imgs/imgnodo_6_7", "up"),
	('7', '8', "Suba las escaleras hasta la segunda planta, luego gire a la derecha y camine recto hasta que la puerta del ascensor se encuentre a su izquierda.","imgs/imgnodo_7_8", "up"),
	('8', '9', "Suba las escaleras hasta la tercera planta, luego gire a la derecha y camine recto hasta que la puerta del ascensor se encuentre a su izquierda.","imgs/imgnodo_8_9", "up"),
	('9', '-3', "Situése de manera que la puerta del ascensor se encuentre a su izquierda. Camine recto hasta que se encuentre a su derecha la puerta del aula 3.5.","imgs/imgnodo_9_-3", "0"),
	('9', '11', "Situése de manera que la puerta del ascensor se encuentre a su izquierda. Camine recto hasta que pueda salir por la izquierda a las escaleras de emergencia.","imgs/imgnodo_9_11", "0"),
	('10', '-4', "Avance por el hall en dirección contraria a la puerta de salida hasta que se encuentre la puerta de la secretaría.","imgs/imgnodo_10_-4", "0"),
	('11', '-3', "Entre al edificio de las aulas, gire a la derecha y camine recto hasta que a su izquierda se encuentre el aula 3.5.","imgs/imgnodo_11_-3", "0"),
	('11', '-5', "Situése de espaldas a la puerta del edificio de las aulas. Entre por la puerta que tiene delante y avance hasta encontrar el despacho 21.","imgs/imgnodo_11_-5", "0"),
	('-1', '1', "Salga por la puerta principal de la biblioteca, camine recto y salga por la puerta de la izquierda.","imgs/imgnodo_-1_1", "0"),
	('2', '1', "Situése de manera que la puerta del ascensor quede a su izquierda. Camine recto, gire por la puerta de la izquierda y suba las escaleras hasta la primera planta.","imgs/imgnodo_2_1", "up"),
	('3', '2', "Situése de espaldas a la puerta, camine recto y gire a la izquierda hasta encontrarse el ascensor a su izquierda.","imgs/imgnodo_3_2", "0"),
	('10', '2', "Situése de espaldas a la puerta de secretaría. Camine recto por la derecha hasta que a su izquierda esté la puerta del ascensor.","imgs/imgnodo_10_2", "0"),
	('10', '3', "Situése de espaldas a la puerta de secretaría. Camino recto por la izquierda hasta que llegue a la puerta que da al exterior.","imgs/imgnodo_10_3", "0"),
	('5', '3', "Situése de espaldas a la puerta, camine recto y gire a la derecha para entrar al edificio principal.","imgs/imgnodo_5_3", "0"),
	('4', '3', "Suba las escaleras y a la derecha se encuentra la puerta de salida.","imgs/imgnodo_4_3", "up"),
	('-2', '4', "Situése de espaldas a la puerta de la cafeteria y camine hacia las escaleras hasta que la puerta del aula de estudios se encuentre a su derecha.","imgs/imgnodo_-2_4", "0"),
	('6', '5', "Situése de manera que el ascensor se encuentre a su izquierda. Camine recto hasta que vea a su izquierda la puerta y salga del edificio.","imgs/imgnodo_6_5", "0"),
	('7', '6', "Situése con el ascensor a su derecha. Avance por el pasillo y baje las escaleras a su izquierda hasta la planta baja.","imgs/imgnodo_7_6", "down"),
	('8', '7', "Situése con el ascensor a su derecha. Avance por el pasillo y baje las escaleras a su izquierda hasta la primera planta.","imgs/imgnodo_8_7", "down"),
	('9', '8', "Situése con el ascensor a su derecha. Avance por el pasillo y baje las escaleras a su izquierda hasta la segunda planta.","imgs/imgnodo_9_8", "down"),
	('-3', '9', "Sitúese con el aula a la izquierda. Camine recto por el pasillo hasta que se encuentre a su derecha la puerta del ascensor.","imgs/imgnodo_-3_9", "0"),
	('11', '9', "Entre al edificio de las aulas, gire a la derecha y camine recto hasta que a su derecha se encuentre la puerta del ascensor.","imgs/imgnodo_11_9", "0"),
	('-4', '10', "Situése de espaldas a la puerta de secretaría y avance hasta estar en medio del hall.","imgs/imgnodo_-4_10", "0"),
	('-3', '11', "Salga del aula, gire a la derecha y camine recto. Cuando se encuentre a su izquierda la puerta de salida a las escaleras de emergencia, salga al exterior.","imgs/imgnodo_-3_11", "0"),
	('-5', '11', "Salga del despacho y situése en dirección a las escaleras de emergencia. Camine recto y salga al exterior.","imgs/imgnodo_-5_11", "0"),
]

HEADER_RUTAS = ["nodo1", "nodo2", "destino"]

g = networkx.Graph()

for i, checkpoint in enumerate(CHECKPOINTS):
	g.add_node(str(i+1), name=checkpoint)
for i, destino in enumerate(DESTINOS):
	g.add_node(str(-i-1), name=destino)

g.add_edge("-1", "1")
g.add_edge("-2", "4")
g.add_edge("-3", "9")
g.add_edge("-3", "11")
g.add_edge("-4", "10")
g.add_edge("-5", "11")
g.add_edge("1", "2")
g.add_edge("2", "3")
g.add_edge("2", "10")
g.add_edge("3", "10")
g.add_edge("3", "5")
g.add_edge("3", "4")
g.add_edge("5", "6")
g.add_edge("6", "7")
g.add_edge("7", "8")
g.add_edge("8", "9")
g.add_edge("9", "11")

tabla_rutas = defaultdict(list) # (nodo1, nodo2) -> lista de destinos a los que se llega desde nodo1 a nodo2

for i_from in range(len(DESTINOS)):
	for i_to in range(len(DESTINOS)):
		if i_from == i_to:
			continue
		i_from_str = str(-i_from - 1)
		i_to_str = str(-i_to - 1)
		shortest_path = networkx.shortest_path(g, i_from_str, i_to_str)
		for i in range(1, len(shortest_path)):
			tabla_rutas[(shortest_path[i-1], shortest_path[i])].append(i_to_str)

# (nodo1, nodo2, destino al que se llega). clave primaria: nodo1, destino
tabla_rutas = set([(nodo1, nodo2, destino) for (nodo1, nodo2), lista_destinos in tabla_rutas.items() for destino in lista_destinos])

# comprobar que por cada (nodo1, destino) solo hay un nodo2
for nodo1 in g.nodes:
	for destino in range(len(DESTINOS)):
		destino = str(-destino - 1)
		if nodo1 == destino: continue
		nodos2 = [elem[1] for elem in tabla_rutas if elem[0] == nodo1 and elem[2] == destino]
		assert len(nodos2) == 1

with open("rutas.csv", "w") as f:
	writer = csv.writer(f)
	writer.writerow(HEADER_RUTAS)
	writer.writerows(tabla_rutas)

with open("instrucciones.csv", "w") as f:
	writer = csv.writer(f)
	writer.writerow(HEADER_INSTRUCCIONES)
	writer.writerows(TABLA_INSTRUCCIONES)

nodos = [(n, data["name"]) for n, data in g.nodes.items()]
with open("nodos.csv", "w") as f:
	writer = csv.writer(f)
	writer.writerow(HEADER_NODOS)
	writer.writerows(nodos)


# CSVs to sqlite database
# https://mungingdata.com/sqlite/create-database-load-csv-python/
DB_FILENAME = "p4.db"
path = Path(DB_FILENAME)
path.unlink(missing_ok=True)
path.touch()
conn = sqlite3.connect(DB_FILENAME)
c = conn.cursor()

c.execute("CREATE TABLE nodos (nodo int, nombre text)")
pandas.read_csv("nodos.csv").to_sql("nodos", conn, if_exists="append", index=False)

c.execute("CREATE TABLE instrucciones (nodo1 int, nodo2 int, instrucciones text, imagen text, direccion text)")
pandas.read_csv("instrucciones.csv").to_sql("instrucciones", conn, if_exists="append", index=False)

c.execute("CREATE TABLE rutas (nodo1 int, nodo2 int, destino int)")
pandas.read_csv("rutas.csv").to_sql("rutas", conn, if_exists="append", index=False)

c.close()
conn.close()


# Copiar db a los assets
shutil.copy(DB_FILENAME, "app/src/main/assets/databases/" + DB_FILENAME)




# for elem in TABLA_INSTRUCCIONES:
# 	key2 = elem[3].split("/")[1]
# 	key = key2.replace("imgnodo_", "").replace("_", ", ")
# 	key2 = key2.replace("-", "m")
# 	print("m.put(new Pair<>(" + key + "), R.drawable." + key2 + ");")
# 	shutil.copyfile("/home/david/Escritorio/NPI/NPI-P3/app/src/main/res/drawable/logougr2.png",
# 	                "/home/david/Escritorio/NPI/NPI-P3/app/src/main/res/drawable/" + key2 + ".png")


# a = networkx.nx_agraph.to_agraph(g)
# a.layout("dot")
# a.draw("salida.png")