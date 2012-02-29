package com.androidrecord.associations;

import com.androidrecord.ActiveRecordBase;

import java.lang.reflect.Field;

public class OneToOneAssociation {
    private ActiveRecordBase owner;
    private ActiveRecordBase owned;
    private String associationName;
    private Field ownedField;
    private Field ownerField;
    private Long ownerId;
    private boolean fulfilled = false;

    public OneToOneAssociation(String associationName, Long ownerId) {
        this.associationName = associationName;
        this.ownerId = ownerId;
    }

    public boolean connectOwner(ActiveRecordBase owner) {
        if (owner == null || isOwnerIdUnexpected(owner.id)) return false;

        saveOwner(owner);
        if (owned != null) connect();
        return true;
    }

    public boolean connectOwned(ActiveRecordBase owned, Long ownerId) {
        if (isOwnerIdUnexpected(ownerId)) return false;
        saveOwned(owned);
        if (owner != null) connect();
        return true;
    }

    private boolean isOwnerIdUnexpected(Long id) {
        return ownerId != null && !ownerId.equals(id);
    }

    private void saveOwner(ActiveRecordBase owner) {
        this.owner = owner;
        lookUpOwnerField();
    }

    private void saveOwned(ActiveRecordBase owned) {
        if (owned == null) return;
        this.owned = owned;
        lookUpOwnedField();
    }

    private void lookUpOwnerField() {
        for (Field field : owner.getClass().getFields()) {
            saveFieldIfHasOne(field);
            saveFieldIfHasMany(field);
        }
    }

    private void saveFieldIfHasOne(Field field) {
        if (field.isAnnotationPresent(HasOne.class) && hasOneMatches(field)) ownerField = field;
    }

    private boolean hasOneMatches(Field field) {
        return field.getAnnotation(HasOne.class).name().equals(associationName);
    }

    private void saveFieldIfHasMany(Field field) {
        if (field.isAnnotationPresent(HasMany.class) && hasManyMatches(field)) ownerField = field;
    }

    private boolean hasManyMatches(Field field) {
        return field.getAnnotation(HasMany.class).name().equals(associationName);
    }

    private void lookUpOwnedField() {
        for (Field field : owned.getClass().getFields()) {
            if (field.isAnnotationPresent(BelongsTo.class)) {
                if (field.getName().equals(associationName)) this.ownedField = field;
            }
        }
    }

    private void connect() {
        try {
            connectOwnerIfHasOne();
            connectOwned();
            fulfilled = true;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void connectOwned() throws IllegalAccessException {
        if (ownedField == null) return;
        ownedField.set(owned, owner);
    }

    private void connectOwnerIfHasOne() throws IllegalAccessException {
        if (ownerField.isAnnotationPresent(HasOne.class)) {
            ownerField.set(owner, owned);
        }
    }

    public boolean isFulfilled() {
        return fulfilled;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public ActiveRecordBase getOwned() {
        return owned;
    }

    public String getName() {
        return associationName;
    }
}
