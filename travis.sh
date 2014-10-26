set -e
set -x

wget http://dl.google.com/android/android-sdk_r23.0.1-linux.tgz
tar -zxf android-sdk_r23.0.1-linux.tgz
wget http://apache.mirror.anlx.net/maven/maven-3/3.2.1/binaries/apache-maven-3.2.1-bin.tar.gz
tar -zxf apache-maven-3.2.1-bin.tar.gz
export ANDROID_HOME=$PWD/android-sdk-linux
export PATH=${PATH}:${ANDROID_HOME}/tools:$PWD/apache-maven-3.2.1/bin
sudo apt-get update
echo yes | sudo apt-get install python-software-properties
echo yes | sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
echo debconf shared/accepted-oracle-license-v1-1 select true | sudo debconf-set-selections
echo debconf shared/accepted-oracle-license-v1-1 seen true |  sudo debconf-set-selections
echo yes | sudo apt-get install --force-yes libgd2-xpm ia32-libs ia32-libs-multiarch xvfb git oracle-java7-installer
echo yes | android update sdk --filter platform-tools,build-tools-21.0.2,android-18,addon-google_apis-google-18,android-10,android-7,extra-android-support,sysimg-16 --no-ui --force
git clone https://github.com/mosabua/maven-android-sdk-deployer.git
cd maven-android-sdk-deployer
mvn install -P 4.3
Xvfb :99 -ac -screen 0 1024x768x24 &
export DISPLAY=:99.0
cd /vagrant
echo adtUsed=true$'\n'lastSdkPath=$ANDROID_HOME > ~/.android/ddms.cfg
mvn --file org.sonatype.aether/pom.xml clean install
mvn -e clean install -Dtycho.showEclipseLog=true
