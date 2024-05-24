(ns sistemas-l.tortugas
  (:require [clojure.math :as Math]))

(defn crear-tortuga
  "Crea una tortuga con estado inicial o basado en el estado dado.

  Parámetros:
    x - Coordenada X (Píxeles).
    y - Coordenada Y (Píxeles).
    o - Orientación del siguiente movimiento (Ángulo).
    p - Estado de la pluma (activa o inactiva).

  Retorna:
    Crea una tortuga con estado inicial o basado en el estado dado"
  ([] {:x           0
       :y           0
       :orientacion 180
       :pluma       true})
  ([x y o p] {:x           x
              :y           y
              :orientacion o
              :pluma       p})
  ([tortuga] (into {} tortuga)))

(defn bajar-pluma [tortuga]
  (assoc tortuga :pluma false))

(defn avanzar [distancia {:keys [x y orientacion] :as tortuga}]
  (let [angRad (* orientacion (/ Math/PI 180))
        dx (* distancia (Math/cos angRad))
        dy (* distancia (Math/sin angRad))]
    (assoc tortuga :x (+ x (int dx)) :y (+ y (int dy)))))

(defn avanzar-y-dibujar [distancia tortuga]
  (let [tortuga-dibujante (-> (avanzar distancia tortuga) (assoc :pluma true))]
    [tortuga-dibujante (bajar-pluma tortuga-dibujante)]))

(defn derecha [angulo tortuga]
  (let [angGrad (get tortuga :orientacion)]
    (merge tortuga {:orientacion (mod (- angGrad angulo) 360)})))

(defn izquierda [angulo tortuga]
  (derecha (* angulo -1) tortuga))


(defn crear-tortugas [op distancia angulo]
  (let [alfabeto {\F #(avanzar distancia %)
                  \G #(avanzar distancia %)
                  \f #(avanzar-y-dibujar distancia %)
                  \g #(avanzar-y-dibujar distancia %)
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