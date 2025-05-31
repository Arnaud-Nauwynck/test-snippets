
export function PromptTextArea({promptedText}) {
    return (
        <div style={{ marginTop: 20 }}>
      <textarea
          style={{
              width: '80%',
              minHeight: '100px',
              padding: '10px',
              borderRadius: '5px',
              border: '1px solid #ccc',
              fontSize: '16px',
              backgroundColor: '#f9f9f9',
              resize: 'none'
          }}
          readOnly
          value={promptedText}
      />
        </div>
    );
}