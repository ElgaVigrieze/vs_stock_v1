package com.company.vs_stock.data.enums;

import java.util.*;

public enum Category {
    SPEAKER(CatValues.SPEAKER),
    BACKLINE(CatValues.BACKLINE),
    MIC(CatValues.MIC),
    CONSOLE(CatValues.CONSOLE),
    AUDIO(CatValues.AUDIO),
    MSPOTLIGHT(CatValues.MSPOTLIGHT),
    NMSPOTLIGHT(CatValues.NMSPOTLIGHT),
    LIGHTS(CatValues.LIGHTS),
    VIDEO(CatValues.VIDEO),
    STAND(CatValues.STAND),
    CABLE(CatValues.CABLE),
    TRUSS(CatValues.TRUSS),
    STAGE(CatValues.STAGE),
    TRANSPORT(CatValues.TRANSPORT),
    WORK(CatValues.WORK),
    MISC(CatValues.MISC);

    public final String value;
    public static List<Category> categoriesPrivate = Arrays.asList(Category.values());

    private Category(String value){
        this.value = value;
    }
//
    private static final Map<String, Category> CAT_VALUES = new HashMap<>();

    static {
        for (Category c: values()) {
            CAT_VALUES.put(c.value, c);
        }
    }


    public static Category valueOfLabel(String label) {
        return  CAT_VALUES.get(label);
    }

    public String getLabel(){
        return this.value;
    }
//
//
//
    public static List<String>getCategoriesPublic() {
        List<String> categories = new ArrayList<>();
        for (Category c: categoriesPrivate)  {
            categories.add(c.getLabel());
        }
        return categories;
}

    public static class CatValues {
        public static final String SPEAKER="tumba";
        public static final String BACKLINE="backline";
        public static final String MIC="mikrofons";
        public static final String CONSOLE="pults";
        public static final String AUDIO="audio piederumi";
        public static final String MSPOTLIGHT="kustīgais prožektors";
        public static final String NMSPOTLIGHT="fiksētais prožektors";
        public static final String LIGHTS = "gaismu tehnika";
        public static final String VIDEO="video";
        public static final String STAND="statīvs";
        public static final String CABLE="kabelis";
        public static final String TRUSS="ferma";
        public static final String STAGE="skatuve";
        public static final String TRANSPORT="transports";
        public static final String WORK="darbs";
        public static final String  MISC="cits";


    }
}
