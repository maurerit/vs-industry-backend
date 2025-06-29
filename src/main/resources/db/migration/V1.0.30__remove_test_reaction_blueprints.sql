delete from INDUSTRY_ACTIVITY_MATERIALS where TYPE_ID in (select TYPE_ID from INV_TYPES where TYPE_NAME = 'Test Reaction Blueprint');
delete from INDUSTRY_ACTIVITY_PRODUCTS where TYPE_ID in (select TYPE_ID from INV_TYPES where TYPE_NAME = 'Test Reaction Blueprint');
delete from INV_TYPES where TYPE_NAME = 'Test Reaction Blueprint';