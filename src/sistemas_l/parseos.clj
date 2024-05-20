(ns sistemas-l.parseos
  (:gen-class)
  (:require [clojure.java.io :as io]
            [clojure.string :as string]))

(defn parse-op "Toma la operación y reemplaza cada caracter por el valor de la regla aplicada a él, si existe.

  Parámetros:
    reglas - Hashmap de reglas a aplicar para parsear.
    op - Operación a parsear.

  Retorna:
    Una nueva cadena generada aplicando las reglas definidas"
  [reglas op]
  (apply str (map #(if-let [regla (get reglas (str %))] regla %) (seq op))))

(defn parse-sl "Lee un archivo .sl, crea y completa un hashmap con el ángulo, axioma y reglas descriptas en el mismo.

  Parámetros:
    ruta-sl - Ruta relativa del archivo a leer.

  Retorna:
    Un nuevo hashmap con el ángulo, axioma y un hashmap de reglas, en base a la información del archivo leído"
  [ruta-sl]
  (with-open [archivo (io/reader ruta-sl)]
    (let [angulo (read-string (.readLine archivo))
          axioma (.readLine archivo)]
      (loop [regla (.readLine archivo) reglas {}]
        (if regla (let [[p s] (string/split regla #" ")]
                    (recur (.readLine archivo),
                           (assoc reglas (string/trim p) (string/trim s))))
                  {:angulo angulo
                   :axioma axioma
                   :reglas reglas})))))