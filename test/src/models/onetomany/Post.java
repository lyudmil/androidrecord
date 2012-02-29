package models.onetomany;

import com.androidrecord.ActiveRecordBase;
import com.androidrecord.associations.BelongsTo;

public class Post extends ActiveRecordBase<Post> {

    @BelongsTo
    public Blog blog;

    public String content;
}
