<script context="module">
    import {get, writable} from 'svelte/store';

    const modalElement = writable();
    const category = writable({});
    const mode = writable('create');
    
    let callback = () => {};
    let modal;

    export function show(newMode, selectedCategory = null) {
        mode.set(newMode);
        
        if (selectedCategory) {
            category.set(JSON.parse(JSON.stringify(selectedCategory)));
        } else {
            category.set({
                id: null,
                name: '',
                displayOrder: 0
            });
        }

        modal = new window.bootstrap.Modal(get(modalElement), {
            backdrop: 'static',
            keyboard: false
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

    async function handleSave() {
        if (!isFormValid) {
            showToast($_('errors.fields_required'), 'error');
            return;
        }

        saving = true;

        try {
            const body = {
                name: $category.name,
                displayOrder: $category.displayOrder
            };

            if ($category.id) body.id = $category.id;

            await ApiUtil.post({
                path: '/api/panel/faq/category/save',
                body
            });

            showToast($_('saved'), 'success');
            callback();
            hide();
        } catch (e) {
            console.error(e);
            showToast($_('error'), 'error');
        } finally {
            saving = false;
        }
    }
</script>

<div class="modal fade" bind:this={$modalElement} tabindex="-1" role="dialog" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">{$mode === 'edit' ? $_('faq.edit_category') : $_('faq.add_category')}</h5>
                <button type="button" class="btn-close" aria-label="Close" on:click={hide}></button>
            </div>
            <div class="modal-body">
                <div class="mb-3">
                    <label for="name" class="form-label">{$_('faq.category_modal.name')}</label>
                    <input type="text" class="form-control" id="name" bind:value={$category.name} />
                </div>

                <div class="mb-3">
                    <label for="displayOrder" class="form-label">{$_('faq.category_modal.order')}</label>
                    <input type="number" class="form-control" id="displayOrder" bind:value={$category.displayOrder} />
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" on:click={hide}>{$_('cancel')}</button>
                <button type="button" class="btn btn-primary" on:click={handleSave} disabled={saving}>
                    {#if saving}
                        <span class="spinner-border spinner-border-sm me-1" role="status" aria-hidden="true"></span>
                    {/if}
                    {$_('save')}
                </button>
            </div>
        </div>
    </div>
</div>
