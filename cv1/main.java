
class Main {

    sealed interface Language permits HumanLanguage, ProgrammingLanguage {}

    enum HumanLanguage implements Language { ENGLISH, CZECH }
    enum ProgrammingLanguage implements Language { C, PASCAL }

    public static void main(String[] Args) {
        
        System.out.println(lang2str(ProgrammingLanguage.PASCAL));
        System.out.println(lang2str(ProgrammingLanguage.C));
        System.out.println(lang2str(HumanLanguage.CZECH));
        System.out.println(lang2str(HumanLanguage.ENGLISH));

    }

    private static String lang2str(Language lang) {
        return switch (lang) {
            case HumanLanguage.ENGLISH      -> "english";
            case HumanLanguage.CZECH        -> "czech";
            case ProgrammingLanguage.C      -> "C";
            case ProgrammingLanguage.PASCAL -> "Pascal";
            default -> "";
        };
    }

}
