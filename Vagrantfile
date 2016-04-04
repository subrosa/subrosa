# -*- mode: ruby -*-
# # vi: set ft=ruby :

require 'fileutils'
require 'open-uri'
require 'tempfile'
require 'yaml'

Vagrant.require_version ">= 1.6.0"

CLUSTER_IP="10.3.0.1"
NODE_IP = "10.10.10.42"
USER_DATA_PATH = File.expand_path("k8s/user-data")
SSL_TARBALL_PATH = File.expand_path("k8s/ssl/controller.tar")

system("(cd k8s; mkdir -p ssl && ./init-ssl-ca ssl)") or abort ("failed generating SSL CA artifacts")
system("(cd k8s; ./init-ssl ssl apiserver controller IP.1=#{NODE_IP},IP.2=#{CLUSTER_IP})") or abort ("failed generating SSL certificate artifacts")
system("(cd k8s; ./init-ssl ssl admin kube-admin)") or abort("failed generating admin SSL artifacts")

Vagrant.configure("2") do |config|
  # always use Vagrant's insecure key
  config.ssh.insert_key = false

  config.vm.box = "coreos-alpha"
  config.vm.box_version = ">= 766.0.0"
  config.vm.box_url = "http://alpha.release.core-os.net/amd64-usr/current/coreos_production_vagrant.json"

  config.vm.provider :virtualbox do |v|
    v.cpus = 1
    v.gui = false
    v.memory = 4096

    # On VirtualBox, we don't have guest additions or a functional vboxsf
    # in CoreOS, so tell Vagrant that so it can be smarter.
    v.check_guest_additions = false
    v.functional_vboxsf     = false
  end

  # plugin conflict
  if Vagrant.has_plugin?("vagrant-vbguest") then
    config.vbguest.auto_update = false
  end

  config.vm.network :private_network, ip: NODE_IP

  config.vm.synced_folder ".", "/vagrant", type: "nfs", mount_options: ['nolock,vers=3,udp,noatime']

  config.vm.provision :file, :source => SSL_TARBALL_PATH, :destination => "/tmp/ssl.tar"
  config.vm.provision :shell, :inline => "mkdir -p /etc/kubernetes/ssl && tar -C /etc/kubernetes/ssl -xf /tmp/ssl.tar", :privileged => true

  config.vm.provision :file, :source => USER_DATA_PATH, :destination => "/tmp/vagrantfile-user-data"
  config.vm.provision :shell, :inline => "mv /tmp/vagrantfile-user-data /var/lib/coreos-vagrant/", :privileged => true

end
