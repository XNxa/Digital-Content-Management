export const environment = {
  production: false,
  api: 'http://localhost:8081/api',
  keycloak: {
    url: 'http://localhost:8080/',
    realm: 'dcm',
    clientId: 'frontend-dcm',
  },
  elasticsearch_enabled: true,
};
