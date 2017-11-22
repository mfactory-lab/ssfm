interface AuthConfig {
  clientID: string;
  domain: string;
  callbackURL: string;
}

export const AUTH_CONFIG: AuthConfig = {
  clientID: 'AUTH0_CLIENT_ID',
  domain: 'AUTH0_DOMAIN',
  callbackURL: 'http://localhost:4000/callback'
};
