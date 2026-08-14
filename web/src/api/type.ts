/** 乘车人 VO */
export interface PassengerVO {
  id?: number;
  memberId?: number;
  name?: string;
  idCard?: string;
  type?: string;
  createTime?: string;
  updateTime?: string;
}

/** 查询列表返回结构 */
export interface PassengerQueryResp {
  success: boolean;
  message?: string;
  content?: { list: PassengerVO[]; total: number };
}

/** 保存返回结构 */
export interface SavePassengerResp {
  success: boolean;
  message?: string;
}

export interface savePassengerReq {
  id?: number | undefined;
  memberId: number | undefined;
  name?: string | undefined;
  idCard?: string | undefined;
  type?: string | undefined;
  createTime?: string | undefined;
  updateTime?: string | undefined;
}
