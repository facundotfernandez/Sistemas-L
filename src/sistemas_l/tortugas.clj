(ns sistemas-l.tortugas
  (:require [clojure.math :as Math]))

(defn crear-tortuga
  "Crea una tortuga con estado inicial o basado en el estado dado.

  Parámetros:
    x - Coordenada X (Píxeles).
    y - Coordenada Y (Píxeles).
    o - Orientación del siguiente movimiento (Ángulo).
    p - Estado de la pluma (arriba o abajo).

  Retorna:
    Crea una tortuga con estado inicial o basado en el estado dado"
  ([] {:x           0
       :y           0
       :orientacion 180
       :pluma       false})
  ([x y o p] {:x           x
              :y           y
              :orientacion o
              :pluma       p})
  ([tortuga] (into {} tortuga)))

(defn avanzar [distancia {:keys [x y orientacion] :as tortuga}]
  (let [angRad (* orientacion (/ Math/PI 180))
        dx (* distancia (Math/cos angRad))
        dy (* distancia (Math/sin angRad))]
    (assoc tortuga :x (+ x (int dx)) :y (+ y (int dy)) :pluma false)))

(defn avanzar-y-dibujar [distancia {:keys [pluma] :as tortuga}]
  (-> (avanzar distancia tortuga)
      (assoc :pluma true)))

(defn derecha [angulo tortuga]
  (let [angGrad (get tortuga :orientacion)]
    (merge tortuga {:orientacion (mod (- angGrad angulo) 360)})))

(defn izquierda [angulo tortuga]
  (derecha (* angulo -1) tortuga))

(defn crear-tortugas [op distancia angulo]
  (let [alfabeto {\F #(avanzar-y-dibujar distancia %)
                  \G #(avanzar-y-dibujar distancia %)
                  \f #(avanzar distancia %)
                  \g #(avanzar distancia %)
                  \+ #(derecha angulo %)
                  \- #(izquierda angulo %)
                  \| #(izquierda 0 %)}
        origen (crear-tortuga)]
    (loop [operaciones op
           tortugas [origen]
           aux (vector)]
      (if (empty? operaciones)
        tortugas
        (let [operacion (first operaciones)
              restantes (rest operaciones)
              tortuga-actual (peek tortugas)]
          (case operacion
            \[ (recur restantes tortugas (conj aux tortuga-actual))
            \] (let [nueva-tortuga (peek aux) tortugas-aux (pop aux)] (recur restantes (conj tortugas nueva-tortuga) tortugas-aux))
            (if (contains? alfabeto operacion) (let [nueva-tortuga ((alfabeto operacion) tortuga-actual)]
                                                 (recur restantes (conj tortugas nueva-tortuga) aux))
                                               (recur restantes tortugas aux))))))))