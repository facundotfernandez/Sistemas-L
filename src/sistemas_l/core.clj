(ns sistemas-l.core
  (:gen-class)
  (:require [sistemas-l.parseos :as parseos]
            [sistemas-l.tortugas :as tortugas]))

(defn -main [ruta-sl iter ruta-svg]
  ; Pruebas iniciales
  (let [parseo-test (parseos/parse-sl "resources/arbol1.sl")] (println parseo-test)
                                                              (println (parseos/parse-op (:reglas parseo-test) (:axioma parseo-test)))
                                                              (println (parseos/parse-op (:reglas parseo-test) (parseos/parse-op (:reglas parseo-test) (:axioma parseo-test))))
                                                              (println (tortugas/crear-tortugas (parseos/parse-op (:reglas parseo-test) (:axioma parseo-test)) 5 90))))

(defn crear-svg [operaciones distancia angulo ruta-svg]
  (let [tortugas (tortugas/crear-tortugas operaciones distancia angulo)
        contenido-svg (parseos/parse2svg tortugas)]
    (spit ruta-svg contenido-svg)))