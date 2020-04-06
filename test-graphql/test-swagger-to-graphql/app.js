require('babel-polyfill');
const express = require('express');
const app = express();
const graphqlHTTP = require('express-graphql');
const graphQLSchema = require('./lib');

const proxyUrl = 'http://localhost:8091';
const pathToSwaggerSchema = `${__dirname}/swagger.json`;
const customHeaders = {
};

graphQLSchema(pathToSwaggerSchema, proxyUrl, customHeaders).then(schema => {
  app.use('/graphql', graphqlHTTP(() => {
    return {
      schema,
      graphiql: true
    };
  }));

  app.listen(8093, 'localhost', () => {
    console.info('http://localhost:8093/graphql');
  });
}).catch(e => {
  console.log(e);
});
