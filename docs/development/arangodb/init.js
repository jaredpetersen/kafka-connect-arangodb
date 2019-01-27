// Create database
const databaseName = 'development';
db._createDatabase(databaseName);
db._useDatabase(databaseName);

// Create collections
db._create('airports');
db._createEdgeCollection('flights');
