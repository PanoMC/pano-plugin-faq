<div class="modal fade" bind:this={$modalElement} tabindex="-1" role="dialog" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">
          {$mode === 'edit' ? $_('faq.edit_category') : $_('faq.add_category')}
        </h5>
        <button
          type="button"
          class="btn-close"
          aria-label={$_('buttons.close')}
          title={$_('buttons.close')}
          on:click={hide}></button>
      </div>
      <div class="modal-body">
        <div class="form-floating mb-3">
          <input
            type="text"
            class="form-control"
            id="name"
            bind:value={$category.name}
            placeholder={$_('faq.category_modal.name')} />
          <label for="name">{$_('faq.category_modal.name')}</label>
        </div>

        <div class="form-floating mb-3">
          <input
            type="number"
            class="form-control"
            id="displayOrder"
            bind:value={$category.displayOrder}
            placeholder={$_('faq.category_modal.order')} />
          <label for="displayOrder">{$_('faq.category_modal.order')}</label>
        </div>
      </div>
      <div class="modal-footer">
        <button
          type="button"
          class="btn btn-secondary w-100"
          on:click={handleSave}
          disabled={saving || !isFormValid || ($mode === 'edit' && !isDirty)}>
          {#if saving}
            <span class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"
            ></span>
          {/if}
          {$_('save')}
        </button>
      </div>
    </div>
  </div>
</div>

<script context="module">
  import { get, writable } from 'svelte/store';

  const modalElement = writable();
  const category = writable({});
  const initialCategory = writable('');
  const mode = writable('create');

  let callback = () => {};
  let modal;

  export function show(newMode, selectedCategory = null) {
    mode.set(newMode);

    const data = selectedCategory ? JSON.parse(JSON.stringify(selectedCategory)) : {
      id: null,
      name: '',
      displayOrder: 0,
    };

    category.set(data);
    initialCategory.set(JSON.stringify(data));

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
  import { showToast } from '@panomc/sdk/toasts';
  import ApiUtil from '@panomc/sdk/utils/api';
  import { _ } from '../../../main';

  let saving = false;

  $: isFormValid = $category.name;
  $: isDirty = JSON.stringify($category) !== $initialCategory;

  async function handleSave() {
    if (!isFormValid) {
      showToast($_('errors.fields_required'));
      return;
    }

    saving = true;

    try {
      const body = {
        name: $category.name,
        displayOrder: $category.displayOrder,
      };

      if ($category.id) body.id = $category.id;

      await ApiUtil.post({
        path: '/api/panel/faq/category/save',
        body,
      });

      showToast(
        $mode === 'create' ? $_('faq.toasts.category_added') : $_('faq.toasts.category_updated'),
      );
      callback();
      hide();
    } catch (e) {
      console.error(e);
      showToast($_('error'));
    } finally {
      saving = false;
    }
  }
</script>
