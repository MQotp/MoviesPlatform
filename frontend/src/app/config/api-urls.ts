import { environment } from '../../environments/environment';

export const API_BASE_URL = environment.apiBaseUrl;

export const API_URLS = {

  AUTH: {
    LOGIN: API_BASE_URL + '/auth/login'
  },
};
