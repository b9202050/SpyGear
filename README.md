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

## L293D接線圖

TODO

## 製作Raspberry Pi OS image ([官網步驟](https://www.raspberrypi.org/documentation/installation/installing-images/mac.md))  

1. 從Raspberry官網download image ([Raspbian](http://downloads.raspberrypi.org/raspbian_latest))  

2. 確認電腦有抓到SD card (假設SD card是在/dev/disk4):  
   `diskutil list`  
   
3. Unmount SD card:  
   `diskutil unmountDisk /dev/disk4`  
   
4. Build OS image to SD card:  
   `sudo dd bs=1m if=2015-05-05-raspbian-wheezy.img of=/dev/disk4`  

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
   Nmap scan report for android-194e1df1da58374f (192.168.1.80)  
   Host is up (0.049s latency).  
   Nmap scan report for dd-mac (192.168.1.82)  
   Host is up (0.00028s latency).  
   Nmap scan report for android-25b5d7efb5511880 (192.168.1.102)  
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
      ├── dist  
      │     └──   
      ├── mjpg_streamer  
      └── webwork  
