(ns sistemas-l.tortugas
  (:require
    [clojure.math :as Math]))

(defn crear-tortuga
  "Parámetros:
    x - Coordenada X (Píxeles).
    y - Coordenada Y (Píxeles).
    o - Orientación del siguiente movimiento (Ángulo).
    p - Estado de la pluma (activa o inactiva).

  Retorna:
    Una tortuga con estado inicial"
  ([] {:x           (double 0)
       :y           (double 0)
       :orientacion 270
       :pluma       true}))

(defn subir-pluma
  "Retorna:
    Una nueva tortuga, peor con la pluma levantada."

  [tortuga]
  (assoc tortuga :pluma false))

(defn bajar-pluma
  "Retorna:
    Una nueva tortuga, peor con la pluma bajada."
  [tortuga]
  (assoc tortuga :pluma true))

(defn avanzar
  "Mueve la tortuga una cierta distancia en la direccion con la pluma bajada de su orientacion actual.

  Parámetros:
    distancia - longitud del movimiento

  Retorna:
    Una nueva tortuga con el estado actualizado luego de avanzar con las nuevas coordenadas (x, y)
    y la pluma bajada."
  [distancia {:keys [x y orientacion] :as tortuga}]
  (let [ang-rad (* orientacion (/ Math/PI 180))
        dx (* distancia (Math/cos ang-rad))
        dy (* distancia (Math/sin ang-rad))]
    (assoc tortuga :x (double (+ x dx)) :y (double (+ y dy)) :pluma true)))

(defn avanzar-sin-dibujar
  "Mueve la tortuga una cierta distancia en la direccion con la pluma subida de su orientacion actual.

  Parámetros:
    distancia - longitud del movimiento

  Retorna:
    Una nueva tortuga con el estado actualizado luego de avanzar con las nuevas coordenadas (x, y)
    y la pluma subida."
  [distancia tortuga]
  (->> tortuga (avanzar distancia) (subir-pluma)))

(defn izquierda
  "Gira la orientación de la tortuga a la izquierda por un ángulo dado a la izquierda.

  Parámetros:
    angulo - desplazamiento en grados con respecto a la orientacion actual de la tortuga.
    tortuga.

  Retorna:
     Una nueva tortuga con el estado actualizado después del giro."
  [angulo tortuga]
  (let [ang-grad (:orientacion tortuga)]
    (merge tortuga {:orientacion (mod (+ ang-grad angulo) 360)})))

(defn derecha
  "Gira la orientación de la tortuga a la izquierda por un ángulo dado a la derecha.

  Parámetros:
    angulo - desplazamiento en grados con respecto a la orientacion actual de la tortuga.
    tortuga.

  Retorna:
     Una nueva tortuga con el estado actualizado después del giro."
  [angulo tortuga]
  (izquierda (- angulo) tortuga))

(defn crear-tortugas
  "Parámetros:
    op - indica la operacion a realizar.
    distancia - Coordenada Y (Píxeles).
    angulo - Orientación del siguiente movimiento (Ángulo).

  Retorna:
    Una colección de estados de gráficos tortuga"
  [op distancia angulo]
  (let [alfabeto {\F #(avanzar distancia %)
                  \G #(avanzar distancia %)
                  \f #(avanzar-sin-dibujar distancia %)
                  \g #(avanzar-sin-dibujar distancia %)
                  \+ #(izquierda angulo %)
                  \- #(derecha angulo %)
                  \| #(derecha 180 %)}]
    (loop [operaciones op
           tortugas [(crear-tortuga)]
           aux (vector)]
      (if (empty? operaciones)
        tortugas
        (let [operacion (first operaciones)
              restantes (rest operaciones)
              tortuga-actual (peek tortugas)]
          (case operacion
            \[ (recur restantes tortugas (conj aux (subir-pluma tortuga-actual)))
            \] (let [nueva-tortuga (peek aux) tortugas-aux (pop aux)] (recur restantes (conj tortugas nueva-tortuga) tortugas-aux))
            (if (contains? alfabeto operacion) (let [nueva-tortuga ((alfabeto operacion) tortuga-actual)]
                                                 (recur restantes (conj tortugas nueva-tortuga) aux))
                                               (recur restantes tortugas aux))))))))