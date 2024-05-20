(ns sistemas-l.core
  (:gen-class)
  (:require [sistemas-l.parseos :as parseos]))

(defn -main [ruta-sl iter ruta-svg]
  ; Pruebas iniciales
  (let [parseo-test (parseos/parse-sl "resources/arbol1.sl")] (println parseo-test)
                                                              (println (parseos/parse-op (:reglas parseo-test) (:axioma parseo-test)))
                                                              (println (parseos/parse-op (:reglas parseo-test) (parseos/parse-op (:reglas parseo-test) (:axioma parseo-test))))))

(defn parse2svg [ruta-svg])