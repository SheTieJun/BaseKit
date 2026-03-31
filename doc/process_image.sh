#!/bin/bash

# 脚本说明：
# 1. 遍历指定目录下的所有图片文件，检查是否包含空字节（0x00）
# 2. 如果包含，则在文件末尾添加一个空字节（0x00）
# 3. 如果不包含，则跳过该文件
# 4. 输出处理统计结果（已修改、已跳过）
# ===================== 路径配置 =====================
SCRIPT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" &>/dev/null && pwd)
PROJECT_ROOT=$(dirname "$SCRIPT_DIR")
RES_DIR="${PROJECT_ROOT}/app/src/main/res"
REAL_RES_DIR=$(realpath "$RES_DIR")

# 日志文件
LOG_FILE="${SCRIPT_DIR}/md5_log.txt"
> "$LOG_FILE"

# 临时文件，用于修复 shell 子进程计数器不生效问题
TMP_MODIFIED="${SCRIPT_DIR}/.tmp_modified"
TMP_SKIPPED="${SCRIPT_DIR}/.tmp_skipped"
echo 0 > "$TMP_MODIFIED"
echo 0 > "$TMP_SKIPPED"

# ===================== 输出信息 =====================
echo "=============================================" | tee "$LOG_FILE"
echo "✅ 脚本目录：$SCRIPT_DIR" | tee -a "$LOG_FILE"
echo "✅ 资源目录：$REAL_RES_DIR" | tee -a "$LOG_FILE"
echo "✅ 日志文件：$LOG_FILE" | tee -a "$LOG_FILE"
echo "=============================================" | tee -a "$LOG_FILE"

if [ ! -d "$REAL_RES_DIR" ]; then
    echo "❌ 错误：res 目录不存在" | tee -a "$LOG_FILE"
    exit 1
fi

# ===================== 遍历并处理图片 =====================
find "$REAL_RES_DIR" -type f \( -iname "*.png" -o -iname "*.jpg" -o -iname "*.jpeg" -o -iname "*.webp" -o -iname "*.gif" \) | while read -r img; do

    # 跳过 mipmap
    if [[ "$img" == *mipmap* ]]; then
        continue
    fi

    # 只处理 drawable
    if [[ "$img" != *drawable* ]]; then
        continue
    fi

    # 判断最后一个字节是否为 0x00
    last_byte=$(tail -c 1 "$img" | od -t x1 | head -n 1 | awk '{print $2}')

    if [ "$last_byte" = "00" ]; then
        echo "⚠️  已跳过（已含空字节）：$img" | tee -a "$LOG_FILE"
        sk=$(cat "$TMP_SKIPPED")
        sk=$((sk + 1))
        echo "$sk" > "$TMP_SKIPPED"
    else
        echo -n -e "\x00" >> "$img"
        echo "✅ 已修改 MD5：$img" | tee -a "$LOG_FILE"
        mod=$(cat "$TMP_MODIFIED")
        mod=$((mod + 1))
        echo "$mod" > "$TMP_MODIFIED"
    fi
done

# ===================== 读取最终统计 =====================
MODIFIED=$(cat "$TMP_MODIFIED")
SKIPPED=$(cat "$TMP_SKIPPED")

# ===================== 输出统计 =====================
echo "" | tee -a "$LOG_FILE"
echo "=============================================" | tee -a "$LOG_FILE"
echo "📊 处理统计结果" | tee -a "$LOG_FILE"
echo "✅ 已修改图片数量：$MODIFIED" | tee -a "$LOG_FILE"
echo "⚠️  已跳过图片数量：$SKIPPED" | tee -a "$LOG_FILE"
echo "=============================================" | tee -a "$LOG_FILE"

# 清理临时文件
rm -f "$TMP_MODIFIED" "$TMP_SKIPPED"