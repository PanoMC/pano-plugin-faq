<div class="modal fade" bind:this={$modalElement} tabindex="-1" role="dialog" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">{$mode === 'edit' ? $_('faq.edit_faq') : $_('faq.add_faq')}</h5>
        <button
          type="button"
          class="btn-close"
          aria-label={$_('buttons.close')}
          on:click={hide}></button>
      </div>
      <div class="modal-body">
        <div class="form-floating mb-3">
          <input
            type="text"
            class="form-control"
            id="question"
            bind:value={$faq.question}
            placeholder={$_('faq.question')} />
          <label for="question">{$_('faq.question')}</label>
        </div>

        <div class="form-floating mb-3">
          <textarea
            id="answer"
            class="form-control"
            style="height: 100px"
            bind:value={$faq.answer}
            placeholder={$_('faq.answer')}></textarea>
          <label for="answer">{$_('faq.answer')}</label>
        </div>

        <div class="form-floating mb-3">
          <select
            id="category"
            class="form-select"
            bind:value={$faq.categoryId}
            aria-label={$_('faq.category')}>
            <option value={null}>{$_('faq.uncategorized')}</option>
            {#each $categories as category}
              <option value={category.id}>{category.name}</option>
            {/each}
          </select>
          <label for="category">{$_('faq.category')}</label>
        </div>

        <div class="form-floating mb-3">
          <input
            type="number"
            class="form-control"
            id="displayOrder"
            bind:value={$faq.displayOrder}
            placeholder={$_('faq.display_order')} />
          <label for="displayOrder">{$_('faq.display_order')}</label>
        </div>

        <div class="mb-3">
          <div class="form-check form-switch">
            <input
              type="checkbox"
              class="form-check-input"
              id="active"
              bind:checked={$faq.active} />
            <label class="form-check-label" for="active">{$_('active')}</label>
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button
          type="button"
          class="btn w-100"
          class:btn-primary={$mode === 'edit'}
          class:btn-secondary={$mode === 'create'}
          on:click={handleSave}
          disabled={saving || !isFormValid || ($mode === 'edit' && !isDirty)}>
          {$_('save')}
        </button>
      </div>
    </div>
  </div>
</div>

<script context="module">
  import { get, writable } from 'svelte/store';

  const modalElement = writable();
  const faq = writable({});
  const initialFaq = writable('');
  const categories = writable([]);
  const mode = writable('create');

  let callback = () => {};
  let modal;

  export function show(newMode, selectedFaq = null, categoryList = []) {
    mode.set(newMode);
    categories.set(categoryList);

    const data = selectedFaq ? JSON.parse(JSON.stringify(selectedFaq)) : {
      id: null,
      question: '',
      answer: '',
      categoryId: null,
      displayOrder: 0,
      active: true,
    };

    if (data.active === undefined) data.active = true;

    faq.set(data);
    initialFaq.set(JSON.stringify(data));

    modal = new window.bootstrap.Modal(get(modalElement), {
      backdrop: 'static',
      keyboard: false,
    });
    modal.show();
  }

  export function hide() {
    modal.hide();
  }

  export function setCallback(newCallback) {
    callback = newCallback;
  }
</script>

<script>
  import ApiUtil from '@panomc/sdk/utils/api';
  import { _, showErrorToast, showSuccessToast } from '../../../main';

  let saving = false;

  // Computed properties for form binding
  $: isFormValid = $faq.question && $faq.answer;
  $: isDirty = JSON.stringify($faq) !== $initialFaq;

  async function handleSave() {
    if (!isFormValid) {
      showErrorToast($_('errors.fields_required'));
      return;
    }

    saving = true;

    try {
      const body = {
        question: $faq.question,
        answer: $faq.answer,
        displayOrder: $faq.displayOrder,
        active: $faq.active,
      };

      if ($faq.id) body.id = $faq.id;
      if ($faq.categoryId) body.categoryId = $faq.categoryId;

      await ApiUtil.post({
        path: '/api/panel/faq/save',
        body,
      });

      showSuccessToast(
        $mode === 'create' ? $_('faq.toasts.faq_added') : $_('faq.toasts.faq_updated'),
      );
      callback();
      hide();
    } catch (e) {
      console.error(e);
      showErrorToast($_('error'));
    } finally {
      saving = false;
    }
  }
</script>
