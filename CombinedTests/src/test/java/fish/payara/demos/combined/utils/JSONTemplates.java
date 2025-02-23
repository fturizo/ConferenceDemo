package fish.payara.demos.combined.utils;

public interface JSONTemplates {

    String SPEAKER = """
    {
        "name" : "%s",
        "organization" : "%s"
    }
    """;

    String SESSION = """
    {
        "title" : "%s",
        "venue" : "%s",
        "date" : "%s",
        "duration" : "%s",
        "speakers" : %s
    }
    """;

    String ATTENDEE = """
    {
        "name" : "%s",
        "email" : "%s"
    }
    """;

    String SESSION_RATING = """
    {
        "sessionId" : "%s",
        "rating" : %d
    }
    """;
}
