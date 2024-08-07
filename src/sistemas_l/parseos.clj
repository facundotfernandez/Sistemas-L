(ns sistemas-l.parseos
  (:require
    [clojure.java.io :as io]
    [clojure.string :as str]))

(defn parse-operacion
  "Aplica reglas de conversión a una operación dada.

  Parámetros:
    reglas - Hashmap de reglas a aplicar para parsear.
    op - Operación a parsear.

  Retorna:
    Una nueva cadena generada aplicando las reglas definidas a cada carácter, si es posible."
  [reglas op]
  (apply str (map #(if-let [regla (get reglas (str %))] regla %) (seq op))))

(defn parse-sl!
  "Lee un archivo .sl, crea y completa un hashmap con el ángulo, axioma y reglas descriptas en el mismo.

  Parámetros:
    ruta-sl - Ruta relativa del archivo a leer.

  Retorna:
    Un nuevo hashmap con el ángulo, axioma y un hashmap de reglas, en base a la información del archivo leído."
  [ruta-sl]
  (with-open [archivo (io/reader ruta-sl)]
    (let [[angulo axioma & reglas] (line-seq archivo)
          reglas (into {} (map #(let [[p s] (str/split % #" ")] [(str/trim p) (str/trim s)])) reglas)]
      {:angulo    (read-string angulo)
       :axioma    axioma
       :reglas    reglas
       :distancia 10})))

(defn parse-operaciones
  "Parsea las operaciones dadas reiteradas veces, según se indique.

  Parámetros:
    reglas - Hashmap de reglas a aplicar para parsear.
    operaciones - Operaciones a evaluar y convertir según reglas.
    iteraciones - Cantidad de veces que se debe parsear la operación original.

  Retorna:
    La operación parseada en su último estado."
  [reglas operaciones iteraciones]
  (if (zero? iteraciones)
    operaciones
    (recur reglas (parse-operacion reglas operaciones) (dec iteraciones))))

(defn format-num
  "Formatea un número flotante y lo retorna con 4 decimales.

  Parámetros:
    num - Número a formatear."
  [num] (format "%.4f" num))

(defn unir-y-reemplazar-comas
  "Une los elementos de la colección dada con un espacio como separador y
  reemplaza las comas por puntos.

  Parámetros:
    coll - Collecion de elementos a procesar."
  [coll]
  (str/replace (str/join " " coll) "," "."))

(defn parse-movimiento
  "Parsea un movimiento para escribir en un path de svg, según los datos dados.

  Parámetros:
    destino - Tortuga de destino del movimiento.

  Retorna:
    Una cadena con los datos del movimiento con coordenadas y su tipo, formateados
    (con '.' como separador de decimales) para usar en un path de svg."
  [destino]
  (let [{:keys [x y pluma]} destino]
    (unir-y-reemplazar-comas [(if pluma " L" " M") (format-num x) (format-num y)])))

(defn actualizar-info
  "Actualiza la información sobre el dibujo del sistema-L.

  Parámetros:
    origen - Estado de la tortuga al inicio del movimiento.
    destino - Estado de la tortuga al final del movimiento.
    info - Información a actualizar.

  Retorna:
    La información actual que contiene las mínimas y máximas coordenadas, y el contenido del path del dibujo, según
    la comparación entre origen y destino dados."
  [origen destino {:keys [contenido min-x min-y max-x max-y] :as info}]
  (let [{x1 :x y1 :y p1 :pluma} origen
        {x2 :x y2 :y p2 :pluma} destino]
    (if (and (= x1 x2) (= y1 y2) (= p1 p2))
      info
      (assoc info :contenido (apply str contenido (parse-movimiento destino))
                  :min-x (min min-x x2)
                  :min-y (min min-y y2)
                  :max-x (max max-x x2)
                  :max-y (max max-y y2)))))

(defn parse2path
  "Genera el contenido de un elemento 'path' SVG a partir de los estados de tortuga.

  Parámetros:
    tortugas - Estados de los gráficos tortuga a representar en un path.

  Retorna:
    Un path generado a partir de los estados de tortugas dados, con estado básico de la pluma (Sin relleno, color negro y grosor 1)."
  [tortugas]
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

(defn crear-info-viewbox
  "Genera la información de un atributo viewBox con los datos dados.

  Parámetros:
    min-x - Coordenada mínima en el eje X.
    max-y - Coordenada mínima en el eje Y.
    min-x - Coordenada máxima en el eje X.
    max-y - Coordenada máxima en el eje Y.
    margen - Margen adicional a añadir alrededor del área del dibujo.

  Retorna:
    Una cadena de texto que representa el atributo viewBox del SVG con los valores calculados y el margen incluido"
  [min-x min-y max-x max-y margen]
  (let [dimensiones [(- min-x margen) (- min-y margen) (+ (* 2 margen) (- max-x min-x)) (+ (* 2 margen) (- max-y min-y))]]
    (unir-y-reemplazar-comas (map format-num dimensiones))))

(defn parse2svg!
  "Crea o sobreescribe un archivo (si ya existe) en base a los gráficos tortuga dados.

  Parámetros:
    tortugas - Estados de los gráficos tortuga a representar en un svg.
    ruta-sl - Ruta relativa del archivo a generar o sobreescribir."
  [tortugas ruta-svg]
  (let [info (parse2path tortugas)
        margen (double 50)
        min-x (:min-x info)
        min-y (:min-y info)
        max-x (:max-x info)
        max-y (:max-y info)]
    (with-open [archivo (io/writer ruta-svg :encoding "UTF-8")]
      (spit archivo (str "<svg viewBox=\""
                         (crear-info-viewbox min-x min-y max-x max-y margen)
                         "\" xmlns=\"http://www.w3.org/2000/svg\">\n"
                         (:contenido info) "\n</svg>")))))