// 本文件由 com.junhao.generate.server.EnumGenerator 自动生成，请勿手工修改。
// 后端 Java 枚举（如 PassengerTypeEnum）改动后，重新运行 EnumGenerator 即可同步。
export interface EnumItem {
  code: string;
  desc: string;
}
export const PASSENGER_TYPE: Record<string, EnumItem> = {ADULT: {code:"1", desc:"成人"},CHILD: {code:"2", desc:"儿童"},STUDENT: {code:"3", desc:"学生"}};

export const PASSENGER_TYPE_ARRAY: EnumItem[] = [{code:"1", desc:"成人"},{code:"2", desc:"儿童"},{code:"3", desc:"学生"}];
