package oelp.mahiti.org.newoepl.database;

/**
 * Created by RAJ ARYAN on 02/08/19.
 */
public class DBConstants {
    public static final String DB_NAME = "OelpDatabase.db";
    public static final int VERSION = 1;
    public static final String DATABASESECRETKEY = "oelp@12345";
    public static final String CREATE_TABLE_IF_NOT_EXIST = "CREATE TABLE IF NOT EXISTS ";
    public static final String ALL_FROM = " * from ";
    public static final String WHERE = " where ";
    public static final String FROM = " from ";
    public static final String COMMA = ", ";
    public static final String ORDER_BY = " order by ";
    public static final String AND = " and ";
    public static final String ASCENDING = " asc";
    public static final String TEXT_PRIMARY_KEY = " TEXT PRIMARY KEY ";
    public static final String INT_PRIMARY_KEY = " INTEGER PRIMARY KEY ";
    public static final String OR = " or ";
    public static final String IN = " in ";
    public static final String DELETE ="delete" ;
    public static final String MAX = "max";
    public static final String OPEN_BRACKET = " ( ";
    public static final String CLOSE_BRACKET = " ) ";
    public static final String AS = " as ";
    public static final String LIMIT = " limit ";
    public static final String OFFSET = " offset ";
    public static final String CAT_TABLE_NAME = "CatalogTable";
    public static final String UUID = "uuid";
    public static final String NAME = "name";
    public static final String CODE = "code";
    public static final String ORDER = "orderOF";
    public static final String COLOR = "colorCode";
    public static final String ICON_PATH = "iconPath";
    public static final String MEDIA_PATH = "mediaPath";
    public static final String ICON_TYPE = "iconType";
    public static final String MEDIA_TYPE = "mediaType";
    public static final String MEDIA_LEVEL_TYPE = "mediaLevelType";
    public static final String DESC = "description";
    public static final String TYPE_CONTENT = "typeContent";
    public static final String CONTENT_UUID = "contentUUID";
    public static final String QUESTION_TABLE = "questionTable";
    public static final String Q_TEXT = "questionText";
    public static final String Q_HELP_TEXT = "helpText";
    public static final String DCF = "DCF";
    public static final String MEDIA_CONTENT = "mediaContent";
    public static final String QUESTION_CHOICES_TABLE = "questionChoices";
    public static final String IS_CORRECT = "isCorrectAns";
    public static final String CHOICE_TEXT = "choiceText";
    public static final String Q_ID = "questionId";
    public static final String ANS_EXPLAIN = "answerExplain";
    public static final String SCORE = "score";
    public static final String NULL = null;
    public static final String NOT_NULL = " IS NOT NULL";
    public static String EQUAL_TO=" = ";
    public static String NOT_EQUAL_TO=" != ";
    public static String EMPTY="''";
    public static String DESCENDING =" DESC ";
    public static String LOC_TABLE_NAME ="LocationTable";
    public static String TEXT_COMMA=" TEXT, ";
    public static String TEXT=" TEXT ";
    public static String INTEGER_COMMA=" INTEGER, ";
    public static String INTEGER=" INTEGER ";
    public static String DATETIME_COMMA=" DATETIME, ";
    public static final String SELECT = "select ";



    public static final String ID = "id";
    public static final String ACTIVE = "active";
    public static final String CREATED = "created";
    public static final String MODIFIED = "modified";
    public static final String CT_NAME = "name";
    public static final String BOUNDARY_LEVEL_TYPE = "boundary_level_type";
    public static final String PARENT = "parent";
    public static final String Q_TYPE="QuestionType";


    private DBConstants() {
    }
}
