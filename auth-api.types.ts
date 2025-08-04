// TypeScript 接口定义文件
// 用于前端调用认证相关API

/**
 * API响应基础结构
 */
export interface ApiResponse<T = any> {
  code: number;
  message: string;
  data: T | null;
  token?: string | null;
  tokenExpiresAt?: number | null;
  timestamp: number;
}

/**
 * 用户信息
 */
export interface UserInfo {
  id: number;
  username: string;
  password: null; // 密码字段永远为null，不会返回
  email: string;
  fullName: string;
  status: 'ACTIVE' | 'INACTIVE';
  roles: string[];
  createdAt: string;
  updatedAt: string;
}

/**
 * 登录请求参数
 */
export interface LoginRequest {
  username: string;
  password: string;
}

/**
 * 注册请求参数
 */
export interface RegisterRequest {
  username: string;
  password: string;
  phone: string;
  email: string;
  fullName: string;
}

/**
 * Token验证响应数据
 */
export interface TokenValidationData {
  valid: boolean;
  username?: string;
  expiresAt?: number;
}

/**
 * Token刷新响应数据
 */
export interface TokenRefreshData {
  token: string;
  expiresAt: number;
}

/**
 * 认证API类
 */
export class AuthAPI {
  private baseURL: string;

  constructor(baseURL: string = 'http://localhost:8080') {
    this.baseURL = baseURL;
  }

  /**
   * 用户登录
   */
  async login(credentials: LoginRequest): Promise<ApiResponse<UserInfo>> {
    const response = await fetch(`${this.baseURL}/api/auth/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(credentials),
    });
    return response.json();
  }

  /**
   * 用户注册
   */
  async register(userData: RegisterRequest): Promise<ApiResponse<UserInfo>> {
    const response = await fetch(`${this.baseURL}/api/auth/register`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(userData),
    });
    return response.json();
  }

  /**
   * 获取当前用户信息
   */
  async getCurrentUser(token: string): Promise<ApiResponse<UserInfo>> {
    const response = await fetch(`${this.baseURL}/api/auth/me`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
      },
    });
    return response.json();
  }

  /**
   * 验证Token
   */
  async validateToken(token: string): Promise<ApiResponse<TokenValidationData>> {
    const response = await fetch(`${this.baseURL}/api/auth/validate`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
      },
    });
    return response.json();
  }

  /**
   * 刷新Token
   */
  async refreshToken(token: string): Promise<ApiResponse<TokenRefreshData>> {
    const response = await fetch(`${this.baseURL}/api/auth/refresh`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
      },
    });
    return response.json();
  }

  /**
   * 检查Token是否即将过期
   * @param expiresAt token过期时间戳
   * @param thresholdMinutes 提前多少分钟认为即将过期，默认5分钟
   */
  isTokenExpiringSoon(expiresAt: number, thresholdMinutes: number = 5): boolean {
    const now = Date.now();
    const timeUntilExpiry = expiresAt - now;
    return timeUntilExpiry < thresholdMinutes * 60 * 1000;
  }

  /**
   * 自动刷新Token（如果即将过期）
   */
  async autoRefreshToken(token: string, expiresAt: number): Promise<string | null> {
    if (this.isTokenExpiringSoon(expiresAt)) {
      try {
        const response = await this.refreshToken(token);
        if (response.code === 200 && response.data) {
          return response.data.token;
        }
      } catch (error) {
        console.error('Token refresh failed:', error);
      }
    }
    return null;
  }
}

/**
 * Token管理工具类
 */
export class TokenManager {
  private static readonly TOKEN_KEY = 'auth_token';
  private static readonly EXPIRES_AT_KEY = 'token_expires_at';

  /**
   * 保存Token和过期时间
   */
  static saveToken(token: string, expiresAt: number): void {
    localStorage.setItem(this.TOKEN_KEY, token);
    localStorage.setItem(this.EXPIRES_AT_KEY, expiresAt.toString());
  }

  /**
   * 获取Token
   */
  static getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  /**
   * 获取Token过期时间
   */
  static getTokenExpiresAt(): number | null {
    const expiresAt = localStorage.getItem(this.EXPIRES_AT_KEY);
    return expiresAt ? parseInt(expiresAt) : null;
  }

  /**
   * 清除Token
   */
  static clearToken(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.EXPIRES_AT_KEY);
  }

  /**
   * 检查Token是否有效（未过期）
   */
  static isTokenValid(): boolean {
    const token = this.getToken();
    const expiresAt = this.getTokenExpiresAt();
    
    if (!token || !expiresAt) {
      return false;
    }
    
    return Date.now() < expiresAt;
  }
}

// 使用示例
/*
const authAPI = new AuthAPI();

// 登录
const loginResult = await authAPI.login({
  username: 'testuser123',
  password: '123456'
});

if (loginResult.code === 200 && loginResult.token && loginResult.tokenExpiresAt) {
  // 保存token
  TokenManager.saveToken(loginResult.token, loginResult.tokenExpiresAt);
  
  // 获取当前用户信息
  const userInfo = await authAPI.getCurrentUser(loginResult.token);
  console.log('Current user:', userInfo.data);
}

// 检查并自动刷新token
const token = TokenManager.getToken();
const expiresAt = TokenManager.getTokenExpiresAt();

if (token && expiresAt) {
  const newToken = await authAPI.autoRefreshToken(token, expiresAt);
  if (newToken) {
    // 更新保存的token
    const newExpiresAt = Date.now() + 24 * 60 * 60 * 1000; // 假设24小时有效期
    TokenManager.saveToken(newToken, newExpiresAt);
  }
}
*/
