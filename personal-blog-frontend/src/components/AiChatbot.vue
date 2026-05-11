<template>
  <div v-if="showChatbot" class="ai-chatbot-root">
    <button
      v-if="!open"
      type="button"
      class="ai-chat-fab"
      aria-label="打开博客问答"
      @click="open = true"
    >
      AI
    </button>
    <div v-else class="ai-chat-panel" role="dialog" aria-label="博客问答">
      <div class="ai-chat-head">
        <span class="ai-chat-title">博客问答</span>
        <button type="button" class="ai-chat-icon-btn" aria-label="清空对话" @click="clearChat">清空</button>
        <button type="button" class="ai-chat-icon-btn" aria-label="关闭" @click="open = false">×</button>
      </div>
      <div ref="scrollRef" class="ai-chat-messages">
        <div
          v-for="(m, idx) in messages"
          :key="idx"
          :class="['ai-chat-bubble', m.role]"
        >
          {{ m.content }}
        </div>
      </div>
      <form class="ai-chat-form" @submit.prevent="send">
        <input
          v-model="draft"
          type="text"
          class="ai-chat-input"
          placeholder="问问这篇文章或博客…"
          autocomplete="off"
        />
        <button type="submit" class="ai-chat-send" :disabled="sending || !draft.trim()">发送</button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, watch, nextTick, computed } from 'vue';
import { useRoute } from 'vue-router';
import { useAuthStore } from '../stores/auth';
import { agentChat, agentChatStream, buildAgentChatQuestion } from '../api/agent';

const route = useRoute();
const authStore = useAuthStore();

const showChatbot = computed(() => route.name === 'ArticleDetail' && authStore.isLoggedIn);

watch(showChatbot, (v) => {
  if (!v) {
    open.value = false;
  }
});

const STORAGE_KEY = 'blog-ai-chat-v1';
const MAX_MESSAGES = 40;

const open = ref(false);
const draft = ref('');
const messages = ref([]);
const sending = ref(false);
const scrollRef = ref(null);

function loadStore() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) return;
    const parsed = JSON.parse(raw);
    if (parsed && Array.isArray(parsed.messages)) {
      messages.value = parsed.messages.slice(-MAX_MESSAGES);
    }
  } catch {
    messages.value = [];
  }
}

function saveStore() {
  try {
    localStorage.setItem(
      STORAGE_KEY,
      JSON.stringify({ messages: messages.value.slice(-MAX_MESSAGES) })
    );
  } catch {
    /* ignore */
  }
}

loadStore();

watch(
  messages,
  () => {
    saveStore();
  },
  { deep: true }
);

watch(open, (v) => {
  if (v) {
    nextTick(() => scrollToBottom());
  }
});

function scrollToBottom() {
  const el = scrollRef.value;
  if (el) el.scrollTop = el.scrollHeight;
}

function clearChat() {
  messages.value = [];
  saveStore();
}

function scopeArticleId() {
  if (route.name !== 'ArticleDetail') return undefined;
  const id = route.params?.id;
  const n = Number(id);
  return Number.isFinite(n) && n > 0 ? n : undefined;
}

async function send() {
  const text = draft.value.trim();
  if (!text || sending.value) return;
  draft.value = '';
  messages.value.push({ role: 'user', content: text });
  messages.value = messages.value.slice(-MAX_MESSAGES);
  const thread = messages.value.map(({ role, content }) => ({ role, content }));
  sending.value = true;
  nextTick(() => scrollToBottom());

  const payloadMsgs = thread.filter((m) => m.role === 'user' || m.role === 'assistant');
  const question = buildAgentChatQuestion(payloadMsgs);
  const articleId = scopeArticleId();
  const chatPayload = { question, ...(articleId != null ? { articleId } : {}) };
  messages.value.push({ role: 'assistant', content: '' });
  const assistantIdx = messages.value.length - 1;

  try {
    let full = '';
    try {
      full = await agentChatStream(chatPayload, (delta) => {
        full += delta;
        messages.value[assistantIdx].content = full;
        messages.value = [...messages.value];
        nextTick(() => scrollToBottom());
      });
    } catch {
      full = await agentChat(chatPayload);
      messages.value[assistantIdx].content = full;
      messages.value = [...messages.value];
    }
    if (!messages.value[assistantIdx].content && full) {
      messages.value[assistantIdx].content = full;
      messages.value = [...messages.value];
    }
  } catch (e) {
    messages.value[assistantIdx].content = e?.message || '请求失败';
    messages.value = [...messages.value];
  } finally {
    sending.value = false;
    messages.value = messages.value.slice(-MAX_MESSAGES);
    nextTick(() => scrollToBottom());
  }
}
</script>

<style scoped>
.ai-chatbot-root {
  position: fixed;
  right: 1.25rem;
  bottom: 1.25rem;
  z-index: 1300;
  font-family: var(--font-ui, inherit);
}

.ai-chat-fab {
  width: 3.25rem;
  height: 3.25rem;
  border-radius: 50%;
  border: none;
  cursor: pointer;
  font-weight: 800;
  font-size: 0.95rem;
  color: #fff;
  background: var(--gradient-cta);
  box-shadow: 0 10px 28px rgba(79, 70, 229, 0.4);
}

.ai-chat-panel {
  width: min(22rem, calc(100vw - 2rem));
  height: min(28rem, calc(100vh - 6rem));
  display: flex;
  flex-direction: column;
  background: var(--color-surface);
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-md);
  overflow: hidden;
}

.ai-chat-head {
  display: flex;
  align-items: center;
  gap: 0.35rem;
  padding: 0.55rem 0.65rem;
  border-bottom: 1px solid var(--color-border);
  background: rgba(248, 250, 252, 0.9);
}

.ai-chat-title {
  flex: 1;
  font-weight: 700;
  font-size: 0.9rem;
}

.ai-chat-icon-btn {
  border: none;
  background: transparent;
  cursor: pointer;
  font-size: 1.05rem;
  line-height: 1;
  padding: 0.25rem 0.45rem;
  color: var(--color-text-muted);
  font-family: inherit;
}

.ai-chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 0.75rem;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.ai-chat-bubble {
  max-width: 92%;
  padding: 0.45rem 0.65rem;
  border-radius: var(--radius-md);
  font-size: 0.86rem;
  line-height: 1.45;
  white-space: pre-wrap;
  word-break: break-word;
}

.ai-chat-bubble.user {
  align-self: flex-end;
  background: var(--color-primary-soft);
  color: var(--color-text);
}

.ai-chat-bubble.assistant {
  align-self: flex-start;
  background: rgba(241, 245, 249, 0.95);
  color: var(--color-text);
}

.ai-chat-bubble.typing {
  opacity: 0.7;
}

.ai-chat-form {
  display: flex;
  gap: 0.4rem;
  padding: 0.55rem;
  border-top: 1px solid var(--color-border);
}

.ai-chat-input {
  flex: 1;
  min-width: 0;
  padding: 0.45rem 0.55rem;
  border-radius: var(--radius-md);
  border: 1px solid var(--color-border);
  font-size: 0.86rem;
  font-family: inherit;
}

.ai-chat-send {
  padding: 0.45rem 0.75rem;
  border-radius: var(--radius-md);
  border: none;
  font-weight: 650;
  font-size: 0.82rem;
  cursor: pointer;
  color: #fff;
  background: var(--gradient-cta);
  font-family: inherit;
}

.ai-chat-send:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
