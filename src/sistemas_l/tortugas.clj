(ns sistemas-l.tortugas
  (:require
    [clojure.math :as Math]))

(defn crear-tortuga
  "Crea una nueva tortuga con la posición y estado iniciales.

  Parámetros:
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
  "Levanta la pluma de la tortuga, para que no dibuje al moverse.

  Parámetros:
    tortuga - La tortuga a modificar.

  Retorna:
    Una nueva tortuga con la pluma levantada."
  [tortuga]
  (assoc tortuga :pluma false))

(defn bajar-pluma
  "Baja la pluma de la tortuga, para que dibuje al moverse.

  Parámetros:
    tortuga - La tortuga a modificar.

  Retorna:
    Una nueva tortuga con la pluma bajada."
  [tortuga]
  (assoc tortuga :pluma true))

(defn avanzar
  "Mueve la tortuga una cierta distancia en la direccion con la pluma bajada de su orientacion actual, con la pluma bajada.

  Parámetros:
    distancia - Longitud del movimiento
    tortuga - La tortuga a mover.

  Retorna:
    Una nueva tortuga con el estado actualizado luego de avanzar con las nuevas coordenadas (x, y)
    y la pluma bajada."
  [distancia {:keys [x y orientacion] :as tortuga}]
  (let [ang-rad (* orientacion (/ Math/PI 180))
        dx (* distancia (Math/cos ang-rad))
        dy (* distancia (Math/sin ang-rad))]
    (bajar-pluma (assoc tortuga :x (double (+ x dx)) :y (double (+ y dy))))))

(defn avanzar-sin-dibujar
  "Mueve la tortuga una cierta distancia en la dirección de su orientación actual, con la pluma levantada.

  Parámetros:
    distancia - Longitud del movimiento
    tortuga - La tortuga a mover.

  Retorna:
    Una nueva tortuga con el estado actualizado luego de avanzar con las nuevas coordenadas (x, y)
    y la pluma subida."
  [distancia tortuga]
  (->> tortuga (avanzar distancia) (subir-pluma)))

(defn izquierda
  "Gira la orientación de la tortuga a la izquierda por un ángulo dado.

  Parámetros:
    angulo - desplazamiento en grados con respecto a la orientacion actual de la tortuga.
    tortuga - La tortuga a girar.

  Retorna:
     Una nueva tortuga con el estado actualizado después del giro."
  [angulo tortuga]
  (let [ang-grad (:orientacion tortuga)]
    (merge tortuga {:orientacion (mod (+ ang-grad angulo) 360)})))

(defn derecha
  "Gira la orientación de la tortuga a la derecha por un ángulo dado.

  Parámetros:
    angulo - desplazamiento en grados con respecto a la orientacion actual de la tortuga.
    tortuga - La tortuga a girar.

  Retorna:
     Una nueva tortuga con el estado actualizado después del giro."
  [angulo tortuga]
  (izquierda (- angulo) tortuga))

(defn crear-tortugas
  "Crea una colección de tortugas basada en las operaciones de un sistema-L.

  Parámetros:
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