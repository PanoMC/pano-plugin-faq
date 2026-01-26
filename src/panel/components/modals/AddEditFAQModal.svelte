<script context="module">
    import {get, writable} from 'svelte/store';

    const modalElement = writable();
    const faq = writable({});
    const categories = writable([]);
    const mode = writable('create');
    
    let callback = () => {};
    let modal;

    export function show(newMode, selectedFaq = null, categoryList = []) {
        mode.set(newMode);
        categories.set(categoryList);
        
        if (selectedFaq) {
            const data = JSON.parse(JSON.stringify(selectedFaq));
            // Ensure isActive is boolean if it comes as string/number
            // Sometimes APIs return boolean as string "true"/"false" or 1/0
            // But here we cloned it effectively. 
            // If the original object had a mismatch, we should fix it here.
            
            // However, the user says "changing it doesn't work". 
            // In the form: <input type="checkbox" ... bind:checked={$faq.isActive}>
            // If the initial value is undefined or null, it might be an issue.
            // Let's force it to be boolean.
            if (data.isActive === undefined) data.isActive = true;
            
            faq.set(data);
        } else {
            faq.set({
                id: null,
                question: '',
                answer: '',
                categoryId: null,
                displayOrder: 0,
                isActive: true
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

    // Computed properties for form binding
    $: isFormValid = $faq.question && $faq.answer;

    async function handleSave() {
        if (!isFormValid) {
            showToast($_('errors.fields_required'), 'error');
            return;
        }

        saving = true;

        try {
            const body = {
                question: $faq.question,
                answer: $faq.answer,
                displayOrder: $faq.displayOrder,
                isActive: $faq.isActive
            };

            if ($faq.id) body.id = $faq.id;
            if ($faq.categoryId) body.categoryId = $faq.categoryId;

            await ApiUtil.post({
                path: '/api/panel/faq/save',
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
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">{$mode === 'edit' ? $_('faq.edit_faq') : $_('faq.add_faq')}</h5>
                <button type="button" class="btn-close" aria-label="Close" on:click={hide}></button>
            </div>
            <div class="modal-body">
                <div class="mb-3">
                    <label for="question" class="form-label">{$_('faq.question')}</label>
                    <input type="text" class="form-control" id="question" bind:value={$faq.question} />
                </div>

                <div class="mb-3">
                    <label for="answer" class="form-label">{$_('faq.answer')}</label>
                    <textarea id="answer" class="form-control" rows="4" bind:value={$faq.answer}></textarea>
                </div>

                <div class="mb-3">
                    <label for="category" class="form-label">{$_('faq.category')}</label>
                    <select id="category" class="form-select" bind:value={$faq.categoryId}>
                        <option value={null}>{$_('faq.uncategorized')}</option>
                        {#each $categories as category}
                            <option value={category.id}>{category.name}</option>
                        {/each}
                    </select>
                </div>

                <div class="mb-3">
                    <label for="displayOrder" class="form-label">{$_('faq.display_order')}</label>
                    <input type="number" class="form-control" id="displayOrder" bind:value={$faq.displayOrder} />
                </div>

                <div class="mb-3 form-check">
                    <input type="checkbox" class="form-check-input" id="isActive" bind:checked={$faq.isActive}>
                    <label class="form-check-label" for="isActive">{$_('faq.active')}</label>
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
