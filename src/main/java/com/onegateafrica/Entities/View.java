package com.onegateafrica.Entities;

public class View {
    /*@JsonView(View.Public.class) entity*/
    /*@JsonView(View.Internal.class) controller*/
    static class Public { }
    static class ExtendedPublic extends Public { }
    static class Internal extends ExtendedPublic { }
}
