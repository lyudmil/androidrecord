package models.onetomany;

import com.androidrecord.ActiveCollection;
import com.androidrecord.ActiveRecordBase;
import com.androidrecord.associations.HasMany;

public class Blog extends ActiveRecordBase<Blog> {

    @HasMany(name = "blog", relatedType = Post.class)
    public ActiveCollection<Post> posts;

}
