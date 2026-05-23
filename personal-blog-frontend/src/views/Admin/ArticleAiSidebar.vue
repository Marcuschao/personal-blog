<template>
  <aside class="article-ai-sidebar" :class="{ collapsed }">
    <button type="button" class="ai-collapse-toggle" @click="collapsed = !collapsed">
      {{ collapsed ? '展开写作助手' : '收起' }}
    </button>
    <div v-show="!collapsed" class="ai-panel-inner">
      <p class="ai-panel-title">写作助手</p>
      
      <div style="margin-bottom: var(--space-4);">
        <label class="ai-label">关键词 / 补充说明（可选）</label>
        <n-input
          v-model:value="helperInput"
          type="textarea"
          :rows="3"
          placeholder="大纲时可填写侧重点…"
        />
      </div>

      <div class="ai-actions" style="margin-bottom: var(--space-4);">
        <n-button :loading="busy === 'outline'" :disabled="!!busy" @click="runOutline" block>
          生成大纲
        </n-button>
        <n-button :loading="busy === 'continue'" :disabled="!!busy" @click="runContinue" block>
          续写内容
        </n-button>
        <n-button :loading="busy === 'polish'" :disabled="!!busy" @click="runPolish" block>
          润色 / 纠错
        </n-button>
      </div>

      <div style="margin-bottom: var(--space-4);">
        <label class="ai-label">生成结果</label>
        <n-input
          v-model:value="resultText"
          type="textarea"
          :rows="10"
          readonly
          placeholder="这里将展示 AI 生成的结果…"
        />
      </div>

      <n-space vertical :size="8">
        <n-space justify="space-between">
          <n-button :disabled="!resultText" @click="copyResult" style="flex: 1;">
            复制
          </n-button>
        </n-space>
        <n-button :disabled="!resultText" @click="applyInsert" block>
          应用到正文（插入光标）
        </n-button>
        <n-button :disabled="!resultText" @click="applyReplace" block>
          替换选中文本
        </n-button>
      </n-space>

      <n-alert v-if="aiError" type="error" style="margin-top: 12px;" :bordered="false">
        {{ aiError }}
      </n-alert>
    </div>
  </aside>
</template>

<script setup>
import { ref } from 'vue';
import { NAlert, NButton, NInput, NSpace } from 'naive-ui';
import { agentEditorContinue, agentEditorOutline, agentEditorPolish } from '../../api/agent';
import { useToastStore } from '../../stores/toast';
import { copyTextToClipboard } from '../../utils/clipboard';

const toastStore = useToastStore();

const props = defineProps({
  title: { type: String, default: '' },
  tagsHint: { type: String, default: '' },
  getContext: { type: Function, required: true },
});

const emit = defineEmits(['apply']);

const collapsed = ref(true);
const helperInput = ref('');
const resultText = ref('');
const busy = ref('');
const aiError = ref('');

const keywordsMerged = () => {
  const a = (props.tagsHint || '').trim();
  const b = helperInput.value.trim();
  if (a && b) return `${a}；${b}`;
  return a || b;
};

const runOutline = async () => {
  aiError.value = '';
  busy.value = 'outline';
  try {
    const ctx = props.getContext();
    resultText.value = await agentEditorOutline({
      title: props.title,
      keywords: keywordsMerged(),
      content: ctx.content,
    });
    toastStore.push('大纲已生成', 'success');
  } catch (e) {
    aiError.value = e?.message || '生成失败';
  } finally {
    busy.value = '';
  }
};

const runContinue = async () => {
  aiError.value = '';
  busy.value = 'continue';
  try {
    const ctx = props.getContext();
    resultText.value = await agentEditorContinue({
      title: props.title,
      keywords: keywordsMerged(),
      content: ctx.content,
      selectionStart: ctx.selectionStart,
      selectionEnd: ctx.selectionEnd,
    });
    toastStore.push('续写完成', 'success');
  } catch (e) {
    aiError.value = e?.message || '续写失败';
  } finally {
    busy.value = '';
  }
};

const runPolish = async () => {
  aiError.value = '';
  busy.value = 'polish';
  try {
    const ctx = props.getContext();
    let selectedText = ctx.selectedText || '';
    if (!selectedText.trim()) {
      const pad = 400;
      const i = ctx.selectionStart;
      selectedText = ctx.content.slice(Math.max(0, i - pad), Math.min(ctx.content.length, i + pad));
    }
    if (!selectedText.trim()) {
      aiError.value = '请先选中需要润色的文本，或输入正文。';
      busy.value = '';
      return;
    }
    resultText.value = await agentEditorPolish({
      title: props.title,
      content: ctx.content,
      selectedText,
      selectionStart: ctx.selectionStart,
      selectionEnd: ctx.selectionEnd,
    });
    toastStore.push('润色完成', 'success');
  } catch (e) {
    aiError.value = e?.message || '润色失败';
  } finally {
    busy.value = '';
  }
};

const copyResult = async () => {
  const ok = await copyTextToClipboard(resultText.value);
  if (ok) {
    toastStore.push('已复制到剪贴板', 'success');
    aiError.value = '';
  } else {
    toastStore.push('复制失败，请检查浏览器权限或使用 HTTPS', 'error');
  }
};

const applyInsert = () => {
  emit('apply', { mode: 'insert', text: resultText.value });
};

const applyReplace = () => {
  emit('apply', { mode: 'replace', text: resultText.value });
};
</script>

<style scoped>
.article-ai-sidebar {
  flex-shrink: 0;
  width: min(100%, 340px);
  background: var(--color-surface);
  border-radius: var(--radius-xl);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-md);
  overflow: hidden;
}

.article-ai-sidebar.collapsed {
  width: auto;
  align-self: flex-start;
}

.ai-collapse-toggle {
  width: 100%;
  padding: 0.65rem 1rem;
  border: none;
  background: var(--color-primary-soft);
  color: var(--color-primary);
  font-weight: 650;
  font-size: 0.85rem;
  cursor: pointer;
  font-family: inherit;
}

.article-ai-sidebar.collapsed .ai-collapse-toggle {
  width: auto;
  min-width: 5rem;
  border-radius: var(--radius-md);
}

.ai-panel-inner {
  padding: 1rem 1.05rem 1.25rem;
}

.ai-panel-title {
  margin: 0 0 0.85rem;
  font-size: 0.95rem;
  font-weight: 700;
  color: var(--color-text);
}

.ai-label {
  display: block;
  font-size: 0.72rem;
  font-weight: 650;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: var(--color-text-muted);
  margin-bottom: 0.35rem;
}

.ai-actions {
  display: flex;
  flex-direction: column;
  gap: 0.45rem;
}

@media (max-width: 960px) {
  .article-ai-sidebar {
    width: 100%;
    order: 2;
  }

  .article-ai-sidebar.collapsed {
    width: 100%;
  }
}
</style>
