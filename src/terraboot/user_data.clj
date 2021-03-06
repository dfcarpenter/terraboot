(ns terraboot.user-data
  (:require [terraboot.core :refer :all]
            [terraboot.utils :refer :all]))

(def beats-user-data
  {:coreos      {:units [{:name "filebeat.service" :command "start" :content (snippet "systemd/filebeat.service")}
                         {:name "metricbeat.service" :command "start" :content (snippet "systemd/metricbeat.service")}
                         {:name "dcos-journalctl-file.service" :command "start" :content (snippet "systemd/dcos-journalctl-file.service")}
                         {:name "copy-bins.service" :command "start" :content (snippet "systemd/copy-bins.service")}]}
   :write_files [{:path    "/etc/beats/filebeat.yml"
                  :content (snippet "system-files/filebeat.yml")}
                 {:path    "/etc/beats/metricbeat.yml"
                  :content (snippet "system-files/metricbeat.yml")}]})

(def beats-user-data-ubuntu
  {:write_files [{:path    "/etc/beats/filebeat.yml"
                  :content (snippet "system-files/filebeat.yml")}
                 {:path    "/etc/beats/metricbeat.yml"
                  :content (snippet "system-files/metricbeat.yml")}
                 {:path    "/etc/init/metricbeat.conf"
                  :content (snippet "upstart/metricbeat.conf")}]
   :runcmd      ["mkdir /opt/bin"
                 "curl -s https://s3.eu-central-1.amazonaws.com/terraboot/bins.tgz | tar xz -C /opt/bin"
                 "service metricbeat start"]})

(def elastalert-user-data
  {:coreos      {:units [{:name "elastalert-rules.service" :content (snippet "systemd/elastalert-rules.service")}
                         {:name "elastalert-rules.timer" :command "start" :enable true :content (snippet "systemd/elastalert-rules.timer")}
                         {:name "elastalert-rules-init.service" :command "start" :enable true :content (snippet "systemd/elastalert-rules-init.service")}]}
   :write_files [{:path "/opt/elastalert/config.yaml"
                  :content (snippet "system-files/elastalert-config.yaml")
                  :permissions "644"}
                 {:path "/opt/elastalert/config.json"
                  :content (snippet "system-files/elastalert-config.json")
                  :permissions "644"}
                 {:path        "/opt/elastalert/repo.key"
                  :content     (snippet "ssh-keys/witan-elastalert.pem")
                  :permissions "600"}
                 {:path        "/opt/elastalert/bin/elastalert-pull.sh"
                  :content     (snippet "system-files/elastalert-pull.sh")
                  :permissions "744"}
                 ]})

(def dockerd-logging
  {:write_files [{:path        "/etc/systemd/system/docker.service.d/journald-logging.conf"
                  :content     (snippet "system-files/dockerd-journald-logging.conf")
                  :permissions "0655"}]})