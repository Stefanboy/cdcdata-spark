package com.cdcdata.spark.utils

object ContantsSQL {

  lazy val ODS_SQL = "select " +
    "ods.ip ," +
    "ods.sessionid," +
    "ods.advertisersid," +
    "ods.adorderid," +
    "ods.adcreativeid," +
    "ods.adplatformproviderid" +
    ",ods.sdkversion" +
    ",ods.adplatformkey" +
    ",ods.putinmodeltype" +
    ",ods.requestmode" +
    ",ods.adprice" +
    ",ods.adppprice" +
    ",ods.requestdate" +
    ",ods.appid" +
    ",ods.appname" +
    ",ods.uuid, ods.device, ods.client, ods.osversion, ods.density, ods.pw, ods.ph" +
    ",latitude_longitude_country_city.longitude" +
    ",latitude_longitude_country_city.latitude" +
    ",latitude_longitude_country_city.country" +
    ",latitude_longitude_country_city.city" +
    ",ods.ispid, ods.ispname" +
    ",ods.networkmannerid, ods.networkmannername, ods.iseffective, ods.isbilling" +
    ",ods.adspacetype, ods.adspacetypename, ods.devicetype, ods.processnode, ods.apptype" +
    ",ods.district, ods.paymode, ods.isbid, ods.bidprice, ods.winprice, ods.iswin, ods.cur" +
    ",ods.rate, ods.cnywinprice, ods.imei, ods.mac, ods.idfa, ods.openudid,ods.androidid" +
    ",ods.rtbprovince,ods.rtbcity,ods.rtbdistrict,ods.rtbstreet,ods.storeurl,ods.realip" +
    ",ods.isqualityapp,ods.bidfloor,ods.aw,ods.ah,ods.imeimd5,ods.macmd5,ods.idfamd5" +
    ",ods.openudidmd5,ods.androididmd5,ods.imeisha1,ods.macsha1,ods.idfasha1,ods.openudidsha1" +
    ",ods.androididsha1,ods.uuidunknow,ods.userid,ods.iptype,ods.initbidprice,ods.adpayment" +
    ",ods.agentrate,ods.lomarkrate,ods.adxrate,ods.title,ods.keywords,ods.tagid,ods.callbackdate" +
    ",ods.channelid,ods.mediatype,ods.email,ods.tel,ods.sex,ods.age from ods left join " +
    "latitude_longitude_country_city on ods.ip=latitude_longitude_country_city.ip where ods.ip is not null"

  lazy val COUNTRY_CITY_SQL =
    "select " +
      "country , " +
      "city , " +
      "count(*) as cnts " +
      "from ods " +
      "group by country, city"

}
