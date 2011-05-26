package models.onetomany;

import com.androidrecord.ActiveRecordBase;
import com.androidrecord.relations.BelongsTo;

public class Post extends ActiveRecordBase<Post> {

    @BelongsTo
    public Blog blog;

    public String content;
}
