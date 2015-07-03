## 事前準備
* [Raspberry Pi 2 Model B](http://www.raspberrypi.com.tw/4063/54/)
* 8 GB MicroSD 記憶卡
* 行動電源 (重量越輕越好)
* 無線USB網路卡 ([Edimax EW-7811Un](http://www.edimax.com.tw/tw/produce_detail.php?pd_id=301&pl1_id=1&pl2_id=68))
* 直流馬達控制晶片 ([L293D](http://www.ti.com/lit/ds/symlink/l293.pdf))
* Webcam ([Logitech C310](http://www.logitech.com/zh-tw/product/hd-webcam-c310))
* 直流馬達 x4
* 齒輪組 x4
* 輪子 x4
* 壓克力板 x2
* Android手機 (Android 4.0.3或更新的版本)
* 其它需要的電子材料，例如麵包板、麵包線、電池等

## 開發環境
* [Java SE 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [NetBeans](https://netbeans.org/downloads/)

## Raspberry Pi 2 Model B Spec  
http://pi4j.com/pins/model-2b-rev1.html  

## L293D接線圖  

![](https://github.com/b9202050/SpyGear/blob/master/pi/SpyGear_L293D.jpg)  

## 製作Raspberry Pi OS image ([官網步驟](https://www.raspberrypi.org/documentation/installation/installing-images/mac.md))  

1. 從Raspberry官網download image ([Raspbian](http://downloads.raspberrypi.org/raspbian_latest))  

2. 確認電腦有抓到SD card (假設SD card是在/dev/disk4):  
   `diskutil list`  
   
3. Unmount SD card:  
   `diskutil unmountDisk /dev/disk4`  
   
4. Build OS image to SD card:  
   `sudo dd bs=1m if=2015-05-05-raspbian-wheezy.img of=/dev/disk4`  
   
## 設定Edimax EW-7811Un無線網卡為hotspot ([參考網頁](http://www.savagehomeautomation.com/projects/raspberry-pi-installing-the-edimax-ew-7811un-usb-wifi-adapte.html))  
1. 檢查network interface config  
   `sudo vi /etc/network/interfaces`  
   
	修改內容如下：
	
	```
	auto lo
	iface lo inet loopback

	iface eth0 inet dhcp

	#auto wlan0
	allow-hotplug wlan0
	#iface wlan0 inet manual
	#wpa-roam /etc/wpa_supplicant/wpa_supplicant.conf

	iface wlan0 inet static
	address 192.168.8.1
	netmask 255.255.255.0
	gateway 192.168.1.1

	#iface default inet dhcp
	pre-up iptables-restore < /etc/iptables.ipv4.nat
	```
2.  安裝DHCP server

	`sudo apt-get install isc-dhcp-server`  
   `sudo vi /etc/dhcp/dhcpd.conf`
   
	修改內容如下：
	
	```
	#
	# Sample configuration file for ISC dhcpd for Debian
	#
	#

	# The ddns-updates-style parameter controls whether or not the server will
	# attempt to do a DNS update when a lease is confirmed. We default to the
	# behavior of the version 2 packages ('none', since DHCP v2 didn't
	# have support for DDNS.)
	ddns-update-style none;

	# option definitions common to all supported networks...
	#option domain-name "example.org";
	#option domain-name-servers ns1.example.org, ns2.example.org;

	default-lease-time 600;
	max-lease-time 7200;

	# If this DHCP server is the official DHCP server for the local
	# network, the authoritative directive should be uncommented.
	authoritative;

	# Use this to send dhcp log messages to a different log file (you also
	# have to hack syslog.conf to complete the redirection).
	log-facility local7;

	# No service will be given on this subnet, but declaring it helps the
	# DHCP server to understand the network topology.

	#subnet 10.152.187.0 netmask 255.255.255.0 {
	#}

	# This is a very basic subnet declaration.

	#subnet 10.254.239.0 netmask 255.255.255.224 {
	#  range 10.254.239.10 10.254.239.20;
	#  option routers rtr-239-0-1.example.org, rtr-239-0-2.example.org;
	#}

	# This declaration allows BOOTP clients to get dynamic addresses,
	# which we don't really recommend.

	#subnet 10.254.239.32 netmask 255.255.255.224 {
	#  range dynamic-bootp 10.254.239.40 10.254.239.60;
	#  option broadcast-address 10.254.239.31;
	#  option routers rtr-239-32-1.example.org;
	#}

	# A slightly different configuration for an internal subnet.
	#subnet 10.5.5.0 netmask 255.255.255.224 {
	#  range 10.5.5.26 10.5.5.30;
	#  option domain-name-servers ns1.internal.example.org;
	#  option domain-name "internal.example.org";
	#  option routers 10.5.5.1;
	#  option broadcast-address 10.5.5.31;
	#  default-lease-time 600;
	#  max-lease-time 7200;
	#}

	# Hosts which require special configuration options can be listed in
	# host statements.   If no address is specified, the address will be
	# allocated dynamically (if possible), but the host-specific information
	# will still come from the host declaration.

	#host passacaglia {
	#  hardware ethernet 0:0:c0:5d:bd:95;
	#  filename "vmunix.passacaglia";
	#  server-name "toccata.fugue.com";
	#}

	# Fixed IP addresses can also be specified for hosts.   These addresses
	# should not also be listed as being available for dynamic assignment.
	# Hosts for which fixed IP addresses have been specified can boot using
	# BOOTP or DHCP.   Hosts for which no fixed address is specified can only
	# be booted with DHCP, unless there is an address range on the subnet
	# to which a BOOTP client is connected which has the dynamic-bootp flag
	# set.
	#host fantasia {
	#  hardware ethernet 08:00:07:26:c0:a5;
	#  fixed-address fantasia.fugue.com;
	#}

	# You can declare a class of clients and then do address allocation
	# based on that.   The example below shows a case where all clients
	# in a certain class get addresses on the 10.17.224/24 subnet, and all
	# other clients get addresses on the 10.0.29/24 subnet.

	#class "foo" {
	#  match if substring (option vendor-class-identifier, 0, 4) = "SUNW";
	#}

	#shared-network 224-29 {
	#  subnet 10.17.224.0 netmask 255.255.255.0 {
	#    option routers rtr-224.example.org;
	#  }
	#  subnet 10.0.29.0 netmask 255.255.255.0 {
	#    option routers rtr-29.example.org;
	#  }
	#  pool {
	#    allow members of "foo";
	#    range 10.17.224.10 10.17.224.250;
	#  }
	#  pool {
	#    deny members of "foo";
	#    range 10.0.29.10 10.0.29.230;
	#  }
	#}

	subnet 192.168.8.0 netmask 255.255.255.0 {
	range 192.168.8.10 192.168.8.50;
	option broadcast-address 192.168.8.255;
	option routers 192.168.8.1;
	default-lease-time 600;
	max-lease-time 7200;
	option domain-name "local";
	option domain-name-servers 8.8.8.8, 8.8.4.4;
	}	
	``` 
   
3. 讓wireless module成為預設DHCP device

	`sudo nano /etc/default/isc-dhcp-server`
	
	修改 `INTERFACES=""` 變成 `INTERFACES="wlan0"`
	
	重啟DHCP server
	
	`sudo service isc-dhcp-server restart`

4. 安裝 access point daemon
	
	`sudo apt-get install hostapd`
	
	`sudo vi /etc/hostapd/hostapd.conf`	
	
	並修改設定檔如下：
	
	```
	interface=wlan0
	#driver=nl80211
	driver=rtl871xdrv
	ssid=SpyGear
	hw_mode=g
	channel=6
	macaddr_acl=0
	auth_algs=1
	ignore_broadcast_ssid=0
	wpa=2
	wpa_passphrase=12345678
	wpa_key_mgmt=WPA-PSK
	wpa_pairwise=TKIP
	rsn_pairwise=CCMP
	```

5. 設定router table

	```
	sudo iptables -t nat -A POSTROUTING -o eth0 -j MASQUERADE
	sudo iptables -A FORWARD -i eth0 -o wlan0 -m state --state RELATED,ESTABLISHED -j ACCEPT
	sudo iptables -A FORWARD -i wlan0 -o eth0 -j ACCEPT
	```

6. sudo reboot

	即可透過如下資訊連上Pi  
	
	```
	SSID: SpyGear
	Password: 12345678
	```
   
## 安裝Webcam串流模組 (mjpg-streamer)  
0. 取得Raspberry Pi的IP (nmap)  
   
   安裝nmap:  
   `brew install nmap`  
   
   掃整個網段:  
   `nmap -sP 192.168.1.0-255`  
  
   掃到使用中的IP list:  
   Starting Nmap 6.47 ( http://nmap.org ) at 2015-05-06 21:32 CST  
   Strange error from connect (65):No route to host  
   Nmap scan report for router.asus.com (192.168.1.1)  
   Host is up (0.0074s latency).  
   Nmap scan report for **raspberrypi** (**192.168.1.62**)  
   Host is up (0.0028s latency).  
   Nmap scan report for android-194e (192.168.1.80)  
   Host is up (0.049s latency).  
   Nmap scan report for my-mac (192.168.1.82)  
   Host is up (0.00028s latency).  
   Nmap scan report for android-25b5 (192.168.1.102)  
   Host is up (0.042s latency).  
   Nmap done: 256 IP addresses (5 hosts up) scanned in 2.36 seconds  
   
1. 登入Raspberry Pi  
   `ssh pi@192.168.1.62`  
   
2. 安裝相關套件  
   `sudo apt-get install subversion`  
   `sudo apt-get install libjpeg8-dev`  
   `sudo apt-get install imagemagick`  
   
3. 下載mjpg-streamer模組  
   `svn co https://mjpg-streamer.svn.sourceforge.net/svnroot/mjpg-streamer mjpg-streamer`  

4. 編譯mjpg-streamer模組  
   `cd mjpg-streamer`  
   `make`  
   
5. 檢查Webcam是否有接上Raspberry Pi  
   `lsusb`  

## 安裝MQTT broker - Mosquitto
0. 取得Raspberry Pi的IP (nmap)  
   參考前面步驟  

1. 登入Raspberry Pi  
   參考前面步驟  

2. 安裝Mosquitto (MQTT Broker Server)  
   `apt-get install mosquitto`  

3. 修改Raspberry Pi設定檔  
   `sudo vi /etc/hosts`  

4. 修改Raspberry Pi的IP  
   `192.168.1.62   RaspberryPi`  

5. 重啟Raspberry Pi  
   `sudo reboot`  

## Raspberry Pi目錄結構  
   
/home/pi/  
   ├── dist  <-- MQTT message  
   ├── mjpg_streamer  <-- Webcam  
   └── webwork  
   
## 複製SpyGearPi到Raspberry Pi  
   
   `scp SpyGear.jar pi@192.168.1.62:/home/pi/dist/`  
   
## 啟動mjpg_streamer服務  
   
   `cd ~/mjpg_streamer`  
   `./mjpg_streamer -i "./input_uvc.so -y  -r QVGA -f 15" -o "./output_http.so -w ./www"`  
   
   丟到背景執行  
   `Ctrl + Z`  
   
   切換到背景  
   `bg`  
   
   切換到前景  
   `fg`  
   
## 啟動SpyGearPi  
   
   `cd ~/dist`  
   `sudo java -jar SpyGearPi.jar`  
