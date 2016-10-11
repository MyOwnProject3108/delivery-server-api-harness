package com.adstream.api.gen;

/**
 * Created by natla on 26/07/2016.
 */
public class MyWrap {

  public Meta meta;

  public Meta getMeta() {
    return meta;
  }

  public MyWrap withMeta(Meta meta) {
    this.meta = meta;
    return this;
  }

  public static class Meta {
    public CommonMeta common;

    public CommonMeta getCommon() {
      return common;
    }

    public Meta withCommon(CommonMeta common) {
      this.common = common;
      return this;
    }

    public static class CommonMeta{

      private String clockNumber;
      private String firstAirDate;
      private String duration;
      private String title;

      public String getClockNumber() {
        return clockNumber;
      }

      public CommonMeta withClockNumber(String clockNumber) {
        this.clockNumber = clockNumber;
        return this;
      }

      public String getfirstAirDate() {
        return firstAirDate;
      }

      public CommonMeta withfirstAirDate(String firstAirDate) {
        this.firstAirDate = firstAirDate;
        return this;
      }

      public String getDuration() {
        return duration;
      }

      public CommonMeta withDuration(String duration) {
        this.duration = duration;
        return this;
      }

      public String getTitle() {
        return title;
      }

      public CommonMeta withTitle(String title) {
        this.title = title;
        return this;
      }

      //public CommonMeta(){}

    }
  }

}
