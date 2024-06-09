(ns sistemas-l.parseos
  (:gen-class)
  (:require [clojure.java.io :as io]
            [clojure.string :as string]))

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
    (let [[angulo axioma & reglas] (line-seq archivo)
          reglas (into {} (map #(let [[p s] (string/split % #" ")] [(string/trim p) (string/trim s)])) reglas)]
      {:angulo    (read-string angulo)
       :axioma    axioma
       :reglas    reglas
       :distancia 10})))

(defn parse-op-final [reglas operaciones iteraciones]
  (if (zero? iteraciones)
    operaciones
    (recur reglas (parse-op reglas operaciones) (dec iteraciones))))

(defn parse-mov [destino]
  (let [{:keys [x y pluma]} destino]
    (string/join " " [(if pluma " L" " M") (format "%.4f" x) (format "%.4f" y)])))

(defn actualizar-info [origen
                       destino
                       {:keys [contenido min-x min-y max-x max-y] :as info}]
  (let [{x1 :x y1 :y p1 :pluma} origen
        {x2 :x y2 :y p2 :pluma} destino]
    (if (and (= x1 x2) (= y1 y2) (= p1 p2))
      info
      (assoc info :contenido (apply str contenido (parse-mov destino))
                  :min-x (min min-x x2)
                  :min-y (min min-y y2)
                  :max-x (max max-x x2)
                  :max-y (max max-y y2)))))

(defn parse2path [tortugas]
  (let []
    (loop [origen (first tortugas)
           restantes (rest tortugas)
           info {:contenido "<path d=\"M 0 0"
                 :min-x     (:x origen)
                 :min-y     (:y origen)
                 :max-x     (:x origen)
                 :max-y     (:y origen)}]
      (if (empty? restantes)
        (assoc info :contenido (str (:contenido info) "\" stroke-width=\"1\" stroke=\"black\" fill=\"none\"></path>"))
        (recur (first restantes) (rest restantes) (actualizar-info origen (first restantes) info))))))

(defn parse2svg [tortugas ruta-svg]
  (let [info (parse2path tortugas)
        margen 50
        min-x (get info :min-x)
        min-y (get info :min-y)
        max-x (get info :max-x)
        max-y (get info :max-y)]
    (with-open [archivo (io/writer ruta-svg :encoding "UTF-8")]
      (spit archivo (str "<svg viewBox=\""
                         (- min-x margen) " "
                         (- min-y margen) " "
                         (+ (* 2 margen) (- max-x min-x)) " "
                         (+ (* 2 margen) (- max-y min-y))
                         "\" xmlns=\"http://www.w3.org/2000/svg\">\n"
                         (get info :contenido) "\n</svg>")))))