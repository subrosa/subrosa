# -*- mode: ruby -*-
# vi: set ft=ruby :

# Vagrantfile API/syntax version. Don't touch unless you know what you're doing!
VAGRANTFILE_API_VERSION = "2"
BOX_NAME = ENV['BOX_NAME'] || "ubuntu/trusty64"

def provision(config, app)
  config.vm.provider :virtualbox do |vb|
    vb.customize ["modifyvm", :id, "--memory", "2048"]
  end
  app.vm.provision :file do |file|
    file.source = "script/vagrant/etc/docker"
    file.destination = "/tmp/docker"
  end
  app.vm.provision :shell, path: "script/vagrant/provision.sh"
end

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
  config.vm.box = BOX_NAME
  config.ssh.forward_agent = true
  config.vm.synced_folder ".", "/vagrant", type: "nfs", mount_options: ['nolock,vers=3,udp,noatime']

  config.vm.define :dev, primary: true do |app|
    app.vm.network :private_network, ip: "10.10.10.42"
    provision config, app
  end

  if File.exists?("Vagrantfile.local")
    eval(File.read("Vagrantfile.local"))
  end
end


