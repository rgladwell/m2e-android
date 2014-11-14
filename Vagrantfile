# -*- mode: ruby -*-
# vi: set ft=ruby :

VAGRANTFILE_API_VERSION = "2"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
  config.vm.box = "hashicorp/precise64"
  config.vm.provision :shell, :path => "travis.sh"
  config.vm.provider "virtualbox" do |vm|
    vm.memory = 3072
    vm.cpus = 2
  end
end
