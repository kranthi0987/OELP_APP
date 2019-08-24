package mahiti.org.oelp.interfaces;

import mahiti.org.oelp.models.CatalogueDetailsModel;
import mahiti.org.oelp.models.GroupModel;

/**
 * Created by RAJ ARYAN on 12/08/19.
 */
public interface ItemClickListerner {
    void onItemClick(CatalogueDetailsModel item);
    void onItemClick(GroupModel item);

}