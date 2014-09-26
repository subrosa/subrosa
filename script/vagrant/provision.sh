#!/bin/bash

uname -a
uname -r

wget -q -O - https://get.docker.io/gpg | apt-key add -
echo deb http://get.docker.io/ubuntu docker main > /etc/apt/sources.list.d/docker.list
apt-get update -qq
apt-get install -q -y --force-yes lxc-docker
apt-get install -q -y lxc
apt-get install -q -y linux-image-generic-lts-trusty
apt-get install -q -y linux-headers-$(uname -r)
apt-get install -q -y build-essential dkms


echo 'Downloading VBox Guest Additions...'
wget -q http://download.virtualbox.org/virtualbox/4.3.8/VBoxGuestAdditions_4.3.8.iso
mkdir /media/VBoxGuestAdditions
mount -o loop,ro VBoxGuestAdditions_4.3.8.iso /media/VBoxGuestAdditions
echo 'yes' | sh /media/VBoxGuestAdditions/VBoxLinuxAdditions.run
rm VBoxGuestAdditions_4.3.8.iso
umount /media/VBoxGuestAdditions
rmdir /media/VBoxGuestAdditions

cp /tmp/docker /etc/default/docker


