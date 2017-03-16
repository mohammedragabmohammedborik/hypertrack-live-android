package io.hypertrack.sendeta.model;

import com.crashlytics.android.Crashlytics;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
import io.realm.RealmSchema;

/**
 * Created by piyush on 27/07/16.
 */
public class DBMigration implements RealmMigration {
    @Override
    public void migrate(final DynamicRealm realm, long oldVersion, long newVersion) {
        try {
            // Access the Realm schema in order to create, modify or delete classes and their fields.
            RealmSchema schema = realm.getSchema();

            /************************************************
             // Version 0
             class Person
             @Required String firstName;
             @Required String lastName;
             int    age;
             // Version 1
             class Person
             @Required String fullName;            // combine firstName and lastName into single field.
             int age;
             ************************************************/
            // Migrate from version 0 to version 1

            if (oldVersion == 0) {
                RealmObjectSchema metaPlaceSchema = schema.get("UserPlace");
                if (metaPlaceSchema != null && metaPlaceSchema.hasField("hyperTrackDestinationID")) {
                    metaPlaceSchema.removeField("hyperTrackDestinationID");
                }

                RealmObjectSchema tripSchema = schema.get("Trip");
                if (tripSchema != null && tripSchema.hasField("hypertrackTripID")) {
                    tripSchema.removeField("hypertrackTripID");
                }

                // Create a new class
                RealmObjectSchema membershipSchema = schema.create("Membership")
                        .addField("accountId", int.class, FieldAttribute.PRIMARY_KEY)
                        .addField("isAccepted", boolean.class)
                        .addField("isRejected", boolean.class)
                        .addField("accountName", String.class);

                RealmObjectSchema userSchema = schema.get("User");
                if (userSchema != null) {
                    if (userSchema.hasField("hypertrackTripID")) {
                        userSchema.removeField("hypertrackTripID");
                    }

                    userSchema.addRealmListField("memberships", membershipSchema)
                            .transform(new RealmObjectSchema.Function() {
                                @Override
                                public void apply(DynamicRealmObject obj) {
                                    DynamicRealmObject defaultMembership = realm.createObject("Membership");
                                    defaultMembership.setInt("accountId", 1);
                                    defaultMembership.setBoolean("isAccepted", true);
                                    defaultMembership.setBoolean("isRejected", false);
                                    defaultMembership.setString("accountName", "Personal");
                                    obj.getList("memberships").add(defaultMembership);
                                }
                            })
                            .addField("selectedMembershipAccountId", int.class);
                }

                oldVersion++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }
}
