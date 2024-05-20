(ns sistemas-l.tortugas)

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
       :orientacion 0
       :pluma       false})
  ([x y o p] {:x           x
              :y           y
              :orientacion o
              :pluma       p}))

(defn adelante [d])

(defn derecha [a])

(defn izquierda [a])

(defn pluma-arriba [])

(defn pluma-abajo [])