package com.turgul.soccer.manager;

public class Constants {
  private Constants() {}

  public static final String DEFAULT_TEAM_COUNTRY = "Spain";
  public static final Long DEFAULT_TEAM_BUDGET = 3_000_000L;
  public static final Long DEFAULT_PLAYER_MARKET_VALUE = 500_000L;
  public static final int PLAYER_MIN_AGE = 18;
  public static final int PLAYER_MAX_AGE = 35;
  public static final int JWT_TOKEN_VALIDITY_IN_MILLIS = 1000 * 60 * 60;
  public static final String TOKEN_TYPE = "Bearer";
}
