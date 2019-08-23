package oelp.mahiti.org.newoepl.interfaces;

import oelp.mahiti.org.newoepl.models.CatalogueDetailsModel;
import oelp.mahiti.org.newoepl.models.GroupModel;

/**
 * Created by RAJ ARYAN on 12/08/19.
 */
public interface ItemClickListerner {
    void onItemClick(CatalogueDetailsModel item);
    void onItemClick(GroupModel item);

}