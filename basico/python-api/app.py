from flask import Flask, jsonify
from flask_cors import CORS
import mysql.connector
import requests
import os

app = Flask(__name__)
CORS(app)

db_config = {
    'host': os.environ.get('DB_HOST', 'mysqldb'),
    'port': 3306,
    'user': 'root',
    'password': os.environ.get('MYSQL_ROOT_PASSWORD', 'eneas2805'),
    'database': os.environ.get('MYSQL_DATABASE', 'basicosd')
}

@app.route('/api/health', methods=['GET'])
def health():
    return jsonify({"status": "OK", "message": "API Python funcionando"})

@app.route('/api/test-exception/<tipo>', methods=['GET'])
def test_exception(tipo):
    try:
        if tipo == 'archivo':
            with open('/archivo_que_no_existe_12345.txt', 'r') as f:
                contenido = f.read()
            return jsonify({"resultado": "Archivo leído", "contenido": contenido})
        elif tipo == 'bd':
            conn = mysql.connector.connect(**db_config)
            cursor = conn.cursor()
            cursor.execute("SELECT * FROM tabla_que_no_existe")
            datos = cursor.fetchall()
            cursor.close()
            conn.close()
            return jsonify({"resultado": "Consulta exitosa", "datos": datos})
        elif tipo == 'pokemon':
            url = "https://pokeapi.co/api/v2/pokemon/999999"
            response = requests.get(url, timeout=10)
            if response.status_code == 404:
                return jsonify({"error": "Pokémon no encontrado"}), 404
            return jsonify({"resultado": response.json()})
        elif tipo == 'division':
            resultado = 10 / 0
            return jsonify({"resultado": resultado})
        else:
            return jsonify({"error": f"Tipo '{tipo}' no reconocido"}), 400
    except FileNotFoundError as e:
        return jsonify({"error": f"Excepción de archivo: {str(e)}"}), 500
    except mysql.connector.Error as e:
        return jsonify({"error": f"Excepción de BD: {str(e)}"}), 500
    except requests.exceptions.RequestException as e:
        return jsonify({"error": f"Excepción de API externa: {str(e)}"}), 500
    except ZeroDivisionError as e:
        return jsonify({"error": f"Excepción matemática: {str(e)}"}), 500
    except Exception as e:
        return jsonify({"error": f"Excepción genérica: {str(e)}"}), 500

if __name__ == '__main__':
    print("=== Iniciando API Python ===")
    app.run(debug=True, host='0.0.0.0', port=5000)