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
  ([] {:x           (double 0)
       :y           (double 0)
       :orientacion 270
       :pluma       true}))

(defn bajar-pluma [tortuga]
  (assoc tortuga :pluma false))

(defn subir-pluma [tortuga]
  (assoc tortuga :pluma true))

(defn avanzar [distancia {:keys [x y orientacion] :as tortuga}]
  (let [angRad (* orientacion (/ Math/PI 180))
        dx (* distancia (Math/cos angRad))
        dy (* distancia (Math/sin angRad))]
    (assoc tortuga :x (double (+ x dx)) :y (double (+ y dy)) :pluma true)))

(defn avanzar-sin-dibujar [distancia tortuga]
  (->> tortuga (subir-pluma) (avanzar distancia) (bajar-pluma)))

(defn izquierda [angulo tortuga]
  (let [angGrad (get tortuga :orientacion)]
    (merge tortuga {:orientacion (mod (+ angGrad angulo) 360)})))

(defn derecha [angulo tortuga]
  (izquierda (- angulo) tortuga))

(defn crear-tortugas [op distancia angulo]
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
            \[ (recur restantes tortugas (conj aux (bajar-pluma tortuga-actual)))
            \] (let [nueva-tortuga (peek aux) tortugas-aux (pop aux)] (recur restantes (conj tortugas nueva-tortuga) tortugas-aux))
            (if (contains? alfabeto operacion) (let [nueva-tortuga ((alfabeto operacion) tortuga-actual)]
                                                 (recur restantes (conj tortugas nueva-tortuga) aux))
                                               (recur restantes tortugas aux))))))))