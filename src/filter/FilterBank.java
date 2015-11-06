package filter;

/**
 * Created by felix on 06.11.2015.
 */
public class FilterBank {

    private static Filter filter;

    public static Filter getFilter() {
        if (filter == null) {
            setFilter(new BlankFilter());
        }
        return  filter;
    }

    public static void setFilter(Filter newFilter) {
        filter = newFilter;
    }
}
