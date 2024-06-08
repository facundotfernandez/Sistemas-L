(ns sistemas-l.parseos
  (:gen-class)
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [sistemas-l.tortugas]))

(defn parse-op
  "Toma la operación y reemplaza cada caracter por el valor de la regla aplicada a él, si existe.

  Parámetros:
    reglas - Hashmap de reglas a aplicar para parsear.
    op - Operación a parsear.

  Retorna:
    Una nueva cadena generada aplicando las reglas definidas"
  [reglas op]
  (apply str (map #(if-let [regla (get reglas (str %))] regla %) (seq op))))

(defn parse-sl
  "Lee un archivo .sl, crea y completa un hashmap con el ángulo, axioma y reglas descriptas en el mismo.

  Parámetros:
    ruta-sl - Ruta relativa del archivo a leer.

  Retorna:
    Un nuevo hashmap con el ángulo, axioma y un hashmap de reglas, en base a la información del archivo leído"
  [ruta-sl]
  (with-open [archivo (io/reader ruta-sl)]
    (let [angulo (read-string (.readLine archivo))
          axioma (.readLine archivo)]
      (loop [regla (.readLine archivo) reglas {}]
        (if regla
          (let [[p s] (string/split regla #" ")]
            (recur (.readLine archivo), (assoc reglas (string/trim p) (string/trim s))))
          {:angulo    angulo
           :axioma    axioma
           :reglas    reglas
           :distancia 10})))))

(defn parse-op-final [reglas operaciones iteraciones]
  (if (zero? iteraciones)
    operaciones
    (recur reglas (parse-op reglas operaciones) (dec iteraciones))))

(defn parse-instruccion [origen destino]
  (let [x1 (:x origen) y1 (:y origen) p1 (:pluma origen)
        x2 (:x destino) y2 (:y destino) p2 (:pluma destino)
        tipo (if p2 " L" " M")]
    (string/join " " [tipo (* -1 x2) y2])))

(defn actualizar-info [origen
                       destino
                       {:keys [contenido min-x min-y max-x max-y] :as info}]
  (let [x1 (:x origen) y1 (:y origen) p1 (:pluma origen)
        x2 (:x destino) y2 (:y destino) p2 (:pluma destino)]
    (if (and (= x1 x2) (= y1 y2) (= p1 p2))
      info
      (assoc info :contenido (apply str contenido (parse-instruccion origen destino))
                  :min-x (min min-x x2)
                  :min-y (min min-y y2)
                  :max-x (max max-x x2)
                  :max-y (max max-y y2)))))

(defn parse2path [tortugas]
  (loop [origen (first tortugas)
         destino (second tortugas)
         restantes (rest tortugas)
         info {:contenido (str "<path d=\"M 0 0")
               :min-x     (:x origen)
               :min-y     (:y origen)
               :max-x     (:x origen)
               :max-y     (:y origen)}]
    (if (empty? restantes)
      (assoc info :contenido (apply str (get info :contenido) "\" stroke-width=\"1\" stroke=\"black\" fill=\"none\"></path>"))
      (recur destino (first restantes) (rest restantes) (actualizar-info origen destino info)))))

(defn parse2svg [tortugas ruta-svg]
  (let [info (parse2path tortugas)
        margen 5]
    (with-open [archivo (io/writer ruta-svg :encoding "UTF-8")]
      (spit archivo (apply str "<svg viewBox=\"" (string/join " " [(- (get info :min-x) margen) (- (get info :min-y) margen) (+ (* 2 margen)(- (get info :max-x) (get info :min-x))) (+ (* 2 margen)(- (get info :max-y) (get info :min-y)))]) "\" xmlns=\"http://www.w3.org/2000/svg\">\n" (apply str (get info :contenido)) "\n</svg>")))))